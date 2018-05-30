package com.delivery.data.db.jpa.repositories;

import com.delivery.data.db.jpa.entities.CousineData;
import com.delivery.data.db.jpa.entities.ProductData;
import com.delivery.data.db.jpa.entities.StoreData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaProductRepositoryTest {

    @Autowired
    private JpaProductRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Configuration
    @AutoConfigurationPackage
    @EntityScan("com.delivery.data.db.jpa.entities")
    static class Config {
    }

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void findByNameContainingIgnoreCase() {
        // given
        CousineData cousineData = entityManager.persistFlushFind(CousineData.newInstance("name"));
        StoreData storeData = entityManager.persistFlushFind(StoreData.newInstance("name", cousineData));

        Arrays.stream(new String[]{"AABC", "ABBC", "ABCC"})
                .forEach(name -> {
                    String description = name;

                    if ("ABBC".equals(name)) {
                        description = "DESCRIPTION";
                    }

                    entityManager.persistAndFlush(ProductData.newInstance(name, description, storeData));
                });

        // when
        List<ProductData> actual = repository.findByNameContainingOrDescriptionContainingAllIgnoreCase("abc", "des");

        // then
        assertThat(actual).hasSize(3).extracting("name").containsOnly("AABC", "ABBC", "ABCC");
    }
}