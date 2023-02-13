package net.petri.springboot.entity;

import lombok.*;

import javax.persistence.*;

@Getter

@Table(name = "example_nets")@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ExampleNets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "net_name")
    private String netName;

    @Column(name = "net_export")
    private String netExport;

}
