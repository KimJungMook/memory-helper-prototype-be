package com.website.military.domain.Entity;
import java.time.Instant;
import java.util.List;

import com.website.military.config.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gptword")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GptWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gpt_word_id", updatable = false)
    private Long gptWordId;
    @Size(min = 1, max = 30, message = "단어는 1자이상 30자이하여야합니다.")
    private String word;
    
    @Convert(converter = StringListConverter.class) // List<String>을 JSON으로 변환
    @Column(columnDefinition = "JSON")
    private List<String> noun;

    @Convert(converter = StringListConverter.class) // List<String>을 JSON으로 변환
    @Column(columnDefinition = "JSON")
    private List<String> verb;

    @Convert(converter = StringListConverter.class) // List<String>을 JSON으로 변환
    @Column(columnDefinition = "JSON")
    private List<String> adjective;

    @Convert(converter = StringListConverter.class) // List<String>을 JSON으로 변환
    @Column(columnDefinition = "JSON")
    private List<String> adverb;

    private Instant createAt;
    
//    @JsonManagedReference // 중복 순환 해결.
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gptword")
//    private List<TestProblems> testproblems;
//    @JsonManagedReference // 중복 순환 해결.
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gptword")
//    private List<Mistakes> mistakes;

    public GptWord(String word, List<String> noun, List<String> verb, List<String> adjective, List<String> adverb){
        this.word = word;
        this.noun = noun;
        this.verb = verb;
        this.adjective = adjective;
        this.adverb = adverb;
        this.createAt = Instant.now();
    }
}