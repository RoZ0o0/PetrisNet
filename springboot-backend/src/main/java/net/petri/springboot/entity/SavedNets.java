package net.petri.springboot.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "saved_user_nets")
public class SavedNets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @Column(name = "save_name")
    private String saveName;

    @Column(name = "net_export")
    private String netExport;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "ref_link")
    private String refLink;
}
