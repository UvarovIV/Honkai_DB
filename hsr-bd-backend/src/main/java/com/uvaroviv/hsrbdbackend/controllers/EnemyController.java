package com.uvaroviv.hsrbdbackend.controllers;


import com.uvaroviv.hsrbdbackend.controllers.DTO.EnemyByIdRq;
import com.uvaroviv.hsrbdbackend.controllers.DTO.EnemySaveRq;
import com.uvaroviv.hsrbdbackend.entities.CombatType;
import com.uvaroviv.hsrbdbackend.entities.Enemy;
import com.uvaroviv.hsrbdbackend.entities.EnemyCombatType;
import com.uvaroviv.hsrbdbackend.repositories.CombatTypeRepository;
import com.uvaroviv.hsrbdbackend.repositories.EnemyCombatTypeRepository;
import com.uvaroviv.hsrbdbackend.services.EnemyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(EnemyService.ENEMY_BASE_PATH)
public class EnemyController {

    private final EnemyService enemyService;

    private final EnemyCombatTypeRepository enemyCombatTypeRepository;
    private final CombatTypeRepository combatTypeRepository;

    public EnemyController(EnemyService enemyService, EnemyCombatTypeRepository enemyCombatTypeRepository, CombatTypeRepository combatTypeRepository) {
        this.enemyService = enemyService;
        this.enemyCombatTypeRepository = enemyCombatTypeRepository;
        this.combatTypeRepository = combatTypeRepository;
    }

    @PostMapping
    public ResponseEntity<?> addEnemy(@Valid @RequestBody EnemySaveRq enemy) {
        Enemy rq = new Enemy(null, enemy.getName(), enemy.getPathToImg());
        long enemyId = enemyService.create(rq);
        rq.setId(enemyId);
        combatTypeRepository.findAll().stream()
                .filter(combatType -> enemy.getCombatTypes().contains(combatType.getName()))
                .map(combatType -> enemyCombatTypeRepository.save(new EnemyCombatType(null, rq, combatType)));

        if ( enemyId == -1 ) {
            return ResponseEntity.badRequest().body("Введена некорректная информация");
        }
        log.info( "Добавление врага {}", enemy );
        return ResponseEntity.created(URI.create("/enemies/" + enemyId)).build();
    }

    @GetMapping
    public List<Enemy> getEnemy() {
        log.info("Вывод всех врагов");
        return enemyService.findAll();
    }

    @PostMapping(EnemyService.ENEMY_BY_ID)
    public Enemy getEnemyById( EnemyByIdRq enemy ) {
        return enemyService.findById( enemy.id() );
    }
}
