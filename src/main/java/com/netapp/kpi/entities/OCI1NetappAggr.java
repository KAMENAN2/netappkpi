package com.netapp.kpi.entities;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor @AllArgsConstructor
public class OCI1NetappAggr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CsvBindByName
    private String Aggregate;
    @CsvBindByName
    private int total;
    @CsvBindByName
    private int used;
    @CsvBindByName
    private int avail;
    @CsvBindByName
    private int capacity;
    private String nodeNameAggregate;
    private Date created_at;
}
