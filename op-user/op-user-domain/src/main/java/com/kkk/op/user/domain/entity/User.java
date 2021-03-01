package com.kkk.op.user.domain.entity;

import com.kkk.op.support.interfaces.Aggregate;
import com.kkk.op.support.types.LongId;

/**
 *
 * @author KaiKoo
 */
public class User implements Aggregate<LongId> {

    private LongId id;

    private String name;

    private String username;

    private String password;

    private String gender;

    private Byte age;

    private String email;

    @Override
    public LongId getId() {
        return this.id;
    }

    public void setId(LongId id) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Byte getAge() {
        return age;
    }

    public void setAge(Byte age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
