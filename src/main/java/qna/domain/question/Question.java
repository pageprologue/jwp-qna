package qna.domain.question;

import qna.domain.BaseTimeEntity;
import qna.domain.answer.Answer;
import qna.domain.answer.Answers;
import qna.domain.user.User;
import qna.exception.CannotDeleteException;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Question extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private Title title;

    @Embedded
    private Contents contents;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "writer_id", foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    private boolean deleted = false;

    @Embedded
    private Answers answers = new Answers();

    protected Question() {
    }

    private Question(String title, String contents, User writer) {
        this.title = new Title(title);
        this.contents = new Contents(contents);
        this.writer = writer;
    }

    public static Question create(String title, String contents, User writer) {
        return new Question(title, contents, writer);
    }

    public void delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
        }
        this.deleted = true;
        answers.delete(loginUser);
    }

    public boolean isOwner(User writer) {
        return this.writer.equals(writer);
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Long getId() {
        return id;
    }

    public Title getTitle() {
        return title;
    }

    public User getWriter() {
        return writer;
    }

    public List<Answer> getAnswers() {
        return answers.getAnswers();
    }
}
