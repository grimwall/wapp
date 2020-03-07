package com.aveng.wapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author apaydin
 */

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DIFF", schema = "VENGEANCE")
public class DiffEntity implements Serializable {

    private static final long serialVersionUID = 124154543132L;

    @Id
    @Column(name = "ID", columnDefinition = "varchar(36)", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    protected String id;

    @Column(name = "DIFF_ID")
    private long diffId;

    @Column(name = "LEFT_TEXT")
    private String leftText;

    @Column(name = "RIGHT_TEXT")
    private String rightText;
}
