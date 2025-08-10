package com.studyParty.entity.article.DTO;

import com.studyParty.entity.article.ArticleComment;
import com.studyParty.entity.user.DTO.UserDTO;
import com.studyParty.entity.user.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class ArticleCommentDTO {
    private ArticleComment articleComment;
    private UserDTO user;
}
