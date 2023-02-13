package net.petri.springboot.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "example_nets")
public class ExampleNets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "net_name")
    private String netName;

    @Column(name = "net_export")
    private String netExport;

}
