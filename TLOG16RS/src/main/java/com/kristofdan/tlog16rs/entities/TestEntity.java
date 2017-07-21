package com.kristofdan.tlog16rs.entities;

import javax.persistence.*;
import lombok.*;

/**
 *
 * @author kristof
 */

@Getter
@Setter
@Entity
public class TestEntity {
    String text;
    @Id
    @GeneratedValue
    Integer id;
}
