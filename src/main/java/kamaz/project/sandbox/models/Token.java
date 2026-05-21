package kamaz.project.sandbox.models;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import kamaz.project.sandbox.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private TokenType type;
    
    @Column(name = "token_value")
    private String tokenValue;
    
    private LocalDateTime expiryDate;
    private boolean disabled;
    
    @ManyToOne
    private User user;

    public Token(TokenType type, String tokenValue, LocalDateTime expiryDate, boolean disabled, User user) {
        this.disabled = disabled;
        this.expiryDate = expiryDate;
        this.type = type;
        this.user = user;
        this.tokenValue = tokenValue;
    }
}