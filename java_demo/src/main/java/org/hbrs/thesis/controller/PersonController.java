package org.hbrs.thesis.controller;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import org.hbrs.thesis.dto.GeneratePersonsDto;
import org.hbrs.thesis.dto.PersonDto;
import org.hbrs.thesis.mappings.PersonMappings;
import org.hbrs.thesis.model.Person;
import org.hbrs.thesis.service.PersonService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PersonController {
    private PersonService personService;
    private static final String BASE_PATH = "/api/persons";

    public PersonController() {
        this.personService = new PersonService();
    }

    public void getPersonEndpoints() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
        getPersonsByPath(BASE_PATH, gson);
        getPersonsByPath(BASE_PATH + "/", gson);
        get(BASE_PATH + "/:id",
                (req, res) -> PersonMappings
                        .mapPersonToPersonDto(personService.getPersonById(Long.parseLong(req.params(":id")))),
                gson::toJson);
    }

    private void getPersonsByPath(String path, Gson gson) {
        get(path, (req, res) -> {
            String numberOfPersonsParam = req.queryParams("numberOfPersons");
            if (numberOfPersonsParam == null) {
                return PersonMappings.mapPersonListToPersonDtoList(personService.getAllPersons());
            } else {
                int numberOfPersons = Integer.parseInt(numberOfPersonsParam);
                return PersonMappings.mapPersonListToPersonDtoList(personService.getNumberOfPersons(numberOfPersons));
            }
        },
                gson::toJson);
    }
    public void insertPerson() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        post(BASE_PATH + "/insert", (req, res) -> {
            PersonDto personDto = gson.fromJson(req.body(), PersonDto.class);
            return personService.insertPersonToDb(PersonMappings.mapPersonDtoToPerson(personDto));
        }, gson::toJson);
    }

    public void generateRandomPersons() {
        Gson gson = new Gson();
        post(BASE_PATH + "/generate_random", (req, res) -> {
            GeneratePersonsDto generatePersonsDto = gson.fromJson(req.body(), GeneratePersonsDto.class);
            return personService.generateRandomPersonsToDB(generatePersonsDto.getNumberOfPersons());
        }, gson::toJson);
    }

    public void removeTable() {
        Gson gson = new Gson();
        delete(BASE_PATH + "/delete/table", (req, res) -> gson.toJson(personService.removeDBTable()));
    }

    public void updatePerson() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        put(BASE_PATH + "/update", (req, res) -> {
            
            Person person = PersonMappings.mapPersonDtoToPerson(gson.fromJson(req.body(), PersonDto.class));
            System.out.println(person.getBirthDate());
            return personService.updatePerson(person);
        }, gson::toJson);
    }
}
