package com.studyParty.entity.article.DTO;

import com.studyParty.entity.article.Article;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ArticleDTO {
    private Article article;
    private UserDTO user;
}
