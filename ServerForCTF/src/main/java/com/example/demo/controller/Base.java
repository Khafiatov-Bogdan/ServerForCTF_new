package com.example.demo.controller;

import com.example.demo.Mobs;
import com.example.demo.Users;
import com.example.demo.Abilities;
import com.example.demo.Promo;
import com.example.demo.Report;
import com.example.demo.service.AbilitiesService;
import com.example.demo.service.CharactersService;
import com.example.demo.service.MobsService;
import com.example.demo.service.UsersService;
import com.example.demo.service.PromoService;
import com.example.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Task;
import com.example.demo.service.TaskService;
import com.example.demo.TaskPWN;
import com.example.demo.service.TaskPWNService;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping()
public class Base {

    @Autowired
    private CharactersService charactersService;

    @Autowired
    private AbilitiesService abilitiesService;

    @Autowired
    private MobsService mobsService;

    @Autowired
    private TaskPWNService taskPWNService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private PromoService promoService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ReportService reportService;



    // ================= USERS =================

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers());
    }

    @GetMapping("/users/{login}")
    public ResponseEntity<?> getUserByLogin(@PathVariable String login) {
        return usersService.getUserByLogin(login)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found"));
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestParam String login,
                                          @RequestParam String password) {
        try {
            // Создаем нового пользователя с 0 очками
            Users created = usersService.createOrUpdateUser(login, password, 0, 0);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ================= POINTS =================

    @GetMapping("/users/{login}/points")
    public ResponseEntity<?> getPoints(@PathVariable String login) {
        try {
            return ResponseEntity.ok(usersService.getPoints(login));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/users/{login}/points/set/{amount}")
    public ResponseEntity<?> setPoints(@PathVariable String login,
                                       @PathVariable int amount) {
        try {
            usersService.setPoints(login, amount);
            return ResponseEntity.ok("Points set to: " + amount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/users/{login}/points/add/{amount}")
    public ResponseEntity<?> addPoints(@PathVariable String login,
                                       @PathVariable int amount) {
        try {
            usersService.addPoints(login, amount);
            return ResponseEntity.ok("Added +" + amount + " points");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/users/{login}/points/subtract/{amount}")
    public ResponseEntity<?> subtractPoints(@PathVariable String login,
                                            @PathVariable int amount) {
        try {
            usersService.subtractPoints(login, amount);
            return ResponseEntity.ok("Subtracted -" + amount + " points");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ================= LAB POINTS =================

    @GetMapping("/users/{login}/points_lab")
    public ResponseEntity<?> getLabPoints(@PathVariable String login) {
        try {
            return ResponseEntity.ok(usersService.getLabPoints(login));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/users/{login}/points_lab/set/{amount}")
    public ResponseEntity<?> setLabPoints(@PathVariable String login,
                                          @PathVariable int amount) {
        try {
            usersService.setLabPoints(login, amount);
            return ResponseEntity.ok("Lab points set to: " + amount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/users/{login}/points_lab/add/{amount}")
    public ResponseEntity<?> addLabPoints(@PathVariable String login,
                                          @PathVariable int amount) {
        try {
            usersService.addLabPoints(login, amount);
            return ResponseEntity.ok("Added +" + amount + " lab points");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/users/{login}/points_lab/subtract/{amount}")
    public ResponseEntity<?> subtractLabPoints(@PathVariable String login,
                                               @PathVariable int amount) {
        try {
            usersService.subtractLabPoints(login, amount);
            return ResponseEntity.ok("Subtracted -" + amount + " lab points");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ================= TOP USERS =================

    @GetMapping("/top3")
    public ResponseEntity<List<Users>> getTop3Users() {
        return ResponseEntity.ok(usersService.getTop3Users());
    }

    @GetMapping("/allNames")
    public ResponseEntity<List<Users.UserNamePointsDTO>> getAllNames() {
        return ResponseEntity.ok(usersService.getAllNames());
    }

    // ================= ABILITIES =================

    @GetMapping("/A/abilities")
    public ResponseEntity<List<Abilities>> getAllAbilities() {
        return ResponseEntity.ok(abilitiesService.findAllAbilities());
    }

    @GetMapping("/A/names")
    public ResponseEntity<List<Abilities.AbilitiesIdName>> getAllAbilitiesName() {
        return ResponseEntity.ok(abilitiesService.findNames());
    }

    @PostMapping("/A")
    public ResponseEntity<Abilities> createAbility(@RequestBody Abilities ability) {
        Abilities created = abilitiesService.appAbility(ability);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/A/{id}")
    public ResponseEntity<Void> deleteAbility(@PathVariable Long id) {
        try {
            abilitiesService.deletAbility(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ================= CHARACTERS =================
    // Используем полный путь com.example.demo.Character, чтобы не конфликтовать с java.lang.Character

    @GetMapping("/C/hell")
    public ResponseEntity<List<com.example.demo.Character>> getAllCharacters() {
        return ResponseEntity.ok(charactersService.getAllCharacters());
    }

    @GetMapping("/C/names")
    public ResponseEntity<List<com.example.demo.Character.CharacterIdName>> getAllCharacterNames() {
        return ResponseEntity.ok(charactersService.getAllNames());
    }

    @PostMapping("/C")
    public ResponseEntity<com.example.demo.Character> createCharacter(@RequestBody com.example.demo.Character character) {
        com.example.demo.Character createdCharacter = charactersService.appCharacter(character);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCharacter);
    }

    @PutMapping("/C/{id}")
    public ResponseEntity<com.example.demo.Character> updateCharacter(@PathVariable Long id,
                                                                      @RequestBody com.example.demo.Character characterDetails) {
        try {
            com.example.demo.Character updatedCharacter = charactersService.updateCharacter(id, characterDetails);
            return ResponseEntity.ok(updatedCharacter);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/C/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) {
        try {
            charactersService.deletCharacter(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ================= MOBS =================

    @GetMapping("/M/mobs")
    public ResponseEntity<List<Mobs>> getAllMobs() {
        return ResponseEntity.ok(mobsService.findAllMobs());
    }

    @GetMapping("/M/names")
    public ResponseEntity<List<Mobs.MobsIdName>> getAllMobsName() {
        return ResponseEntity.ok(mobsService.findNames());
    }

    @PostMapping("/M")
    public ResponseEntity<Mobs> createMobs(@RequestBody Mobs mobs) {
        Mobs created = mobsService.appMobs(mobs);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/M/{id}")
    public ResponseEntity<Void> deleteMobs(@PathVariable Long id) {
        try {
            mobsService.deletMobs(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
// ================= TASKS =================

    // Получить все незавершённые задания
    @GetMapping("/tasks/available")
    public ResponseEntity<List<Task>> getAvailableTasks() {
        return ResponseEntity.ok(taskService.getAvailableTasks());
    }

    @PostMapping("/tasks/{title}/solve")
    public ResponseEntity<Map<String, Object>> solveTaskByTitle(@PathVariable String title) {
        try {
            Task task = taskService.markTaskSolvedByTitle(title);
            Map<String, Object> response = Map.of(
                    "success", Boolean.TRUE,
                    "title", task.getTitle(),
                    "points", Integer.valueOf(task.getPoints())
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = Map.of(
                    "success", Boolean.FALSE,
                    "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Метод getTaskStatusByTitle
    @GetMapping("/tasks/{title}/status")
    public ResponseEntity<Map<String, Object>> getTaskStatusByTitle(@PathVariable String title) {
        return taskService.getTaskByTitle(title)
                .map(task -> {
                    Map<String, Object> response = Map.of(
                            "title", task.getTitle(),
                            "solved", Boolean.valueOf(task.isSolved()),
                            "points", Integer.valueOf(task.getPoints())
                    );
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Task not found")));
    }

    // ================= TASKS PWN =================

    // Получить все незавершённые задания
    @GetMapping("/taskspwn/available")
    public ResponseEntity<List<TaskPWN>> getAvailableTasksPWN() {
        return ResponseEntity.ok(taskPWNService.getAvailableTasks());
    }

    @PostMapping("/taskspwn/{title}/solve")
    public ResponseEntity<Map<String, Object>> solveTaskPWNByTitle(@PathVariable String title) {
        try {
            TaskPWN task = taskPWNService.markTaskSolvedByTitle(title);
            Map<String, Object> response = Map.of(
                    "success", Boolean.TRUE,
                    "title", task.getTitle(),
                    "points", Integer.valueOf(task.getPoints())
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = Map.of(
                    "success", Boolean.FALSE,
                    "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Метод getTaskPWNStatusByTitle
    @GetMapping("/taskspwn/{title}/status")
    public ResponseEntity<Map<String, Object>> getTaskPWNStatusByTitle(@PathVariable String title) {
        return taskPWNService.getTaskByTitle(title)
                .map(task -> {
                    Map<String, Object> response = Map.of(
                            "title", task.getTitle(),
                            "solved", Boolean.valueOf(task.isSolved()),
                            "points", Integer.valueOf(task.getPoints())
                    );
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Task not found")));
    }

    // ================= PROMO =================

    @PostMapping("/promo/use")
    public ResponseEntity<Map<String, Object>> usePromo(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Промокод не указан",
                    "points", 0
            ));
        }

        Optional<Promo> promoOpt = promoService.usePromoCode(code);

        if (promoOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Промокод недоступен или уже использован",
                    "points", 0
            ));
        }

        Promo promo = promoOpt.get();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Промокод активирован",
                "points", promo.getPoints()
        ));
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/report/save")
    public Report saveReport(@RequestBody Report report) {
        return reportService.saveOrUpdateReport(report);
    }

}
