package ru.practicum.mainservice.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Нужен для того, чтобы сериализовать hibernate proxy в объект.
// Во время запроса к базе данных подтягиваются Hibernate proxy -
// это по сути просто id связанных объектов из родительской базы.
// Но если эти связанные proxy добавить в DTO и отправить к клиенту,
// то тогда произойдут вызовы этих объектов из базы данных и упакуются в Json.
@Configuration
public class JacksonConfig {
    @Bean
    public Hibernate5Module hibernate5Module() {
        Hibernate5Module module = new Hibernate5Module();
        module.enable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);

        // А эта настройка при той же ситуации будет передавать клиенту только id связанных объектов.
        // module.enable(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);

        return module;
    }
}
