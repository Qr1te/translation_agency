package com.qritiooo.translationagency.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cat_tools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatTool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String vendor;
    private String currentVersion;
    private Boolean cloudBased;

    @OneToMany(mappedBy = "tool", fetch = FetchType.LAZY)
    private List<TranslatorTool> translatorTools = new ArrayList<>();
}
