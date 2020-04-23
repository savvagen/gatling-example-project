package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.gatling.core.scenario.Scenario;
import io.gatling.core.scenario.Simulation;

@JsonIgnoreProperties("id")
public class JavaUser {

    public int id;
    public String name;
    public String username;
    public String email;

    public JavaUser(){}

    public JavaUser(int id, String name, String username, String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public JavaUser(String name, String username, String email) {
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
