package simplforum.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractPersistable;

/** Model for a user account.
 */
@Entity
@JsonIgnoreProperties({"new", "password"})
public class Account extends AbstractPersistable<Long> {
    @NotBlank
    @Column(unique = true)
    @Length(min=3, max = 25)
    private String name;
    @NotBlank
    @Length(min=5)
    private String password;
    @NotNull
    private short type;
    @NotNull
    private boolean disabled;

    public Account() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
