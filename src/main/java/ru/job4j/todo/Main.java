package ru.job4j.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.job4j.todo.util.TimezoneUtil;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * Program starting point.
 * Starting URLs in browser: localhost:8080 or localhost:8080/index
 */
@SpringBootApplication
public class Main {

    @PostConstruct
    void initTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(TimezoneUtil.DEFAULT_TIMEZONE_ID));
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}