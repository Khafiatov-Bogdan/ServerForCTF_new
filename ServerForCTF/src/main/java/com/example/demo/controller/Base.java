package com.example.demo.controller;

import com.example.demo.Character;
import com.example.demo.Mobs;
import com.example.demo.Users;
import com.example.demo.service.AbilitiesService;
import com.example.demo.service.CharactersService;
import com.example.demo.service.MobsService;
import com.example.demo.Abilities;
import com.example.demo.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private UsersService usersService;


    // ==========================
    //      USERS ENDPOINTS
    // ==========================

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
    public ResponseEntity<?> registerUser(
            @RequestParam String login,
            @RequestParam String password) {
        try {
            Users created = usersService.registerUser(login, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // =============== POINTS API ===============

    @GetMapping("/users/{login}/points")
    public ResponseEntity<?> getPoints(@PathVariable String login) {
        try {
            int points = usersService.getPoints(login);
            return ResponseEntity.ok(points);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/users/{login}/points/set/{amount}")
    public ResponseEntity<?> setPoints(
            @PathVariable String login,
            @PathVariable int amount) {
        try {
            usersService.setPoints(login, amount);
            return ResponseEntity.ok("Points set to: " + amount);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/users/{login}/points/add/{amount}")
    public ResponseEntity<?> addPoints(
            @PathVariable String login,
            @PathVariable int amount) {
        try {
            usersService.addPoints(login, amount);
            return ResponseEntity.ok("Added +" + amount + " points");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/users/{login}/points/subtract/{amount}")
    public ResponseEntity<?> subtractPoints(
            @PathVariable String login,
            @PathVariable int amount) {
        try {
            usersService.subtractPoints(login, amount);
            return ResponseEntity.ok("Subtracted -" + amount + " points");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // =============== TOP USERS (перенесено из первого файла!) ===============

    @GetMapping("/top3")
    public ResponseEntity<List<Users.UserNamePointsDTO>> getTop3Users() {
        List<Users> top = usersService.getTop3Users();

        List<Users.UserNamePointsDTO> dto = top.stream()
                .map(u -> new Users.UserNamePointsDTO(u.getLogin(), u.getPoints()))
                .toList();

        return ResponseEntity.ok(dto);
    }


    @GetMapping("/allNames")
    public ResponseEntity<List<Users.UserNamePointsDTO>> getAllNames() {
        return ResponseEntity.ok(usersService.getAllNames());
    }


    // ==========================
    //      ABILITIES
    // ==========================

    @GetMapping("/A/abilities")
    public ResponseEntity<List<Abilities>> getAllAbilities() {
        List<Abilities> abilities = abilitiesService.findAllAbilities();
        return ResponseEntity.ok(abilities);
    }

    @GetMapping("/A/names")
    public ResponseEntity<List<Abilities.AbilitiesIdName>> getAllAbilitiesName() {
        List<Abilities.AbilitiesIdName> abilities = abilitiesService.findNames();
        return ResponseEntity.ok(abilities);
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


    // ==========================
    //      CHARACTERS
    // ==========================

    @GetMapping("/C/hell")
    public ResponseEntity<List<Character>> getAllCharacters() {
        List<Character> characters = charactersService.getAllCharacters();
        return ResponseEntity.ok(characters);
    }

    @GetMapping("/C/names")
    public ResponseEntity<List<Character.CharacterIdName>> getAllName() {
        return ResponseEntity.ok(charactersService.getAllNames());
    }

    @PostMapping("/C")
    public ResponseEntity<Character> createUser(@RequestBody Character user) {
        Character createdUser = charactersService.appCharacter(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/C/{id}")
    public ResponseEntity<Character> updateUser(@PathVariable Long id, @RequestBody Character userDetails) {
        try {
            Character updatedUser = charactersService.updateCharacter(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/C/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            charactersService.deletCharacter(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // ==========================
    //      MOBS
    // ==========================

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
        return ResponseEntity.status(HttpStatus.CREATED).body(mobsService.appMobs(mobs));
    }

    @DeleteMapping("/M/{id}")
    public ResponseEntity<Void> deleteMobs(@PathVariable Long id) {
        try {
            mobsService.deletMobs(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
