import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import model.UserEntity;

@ManagedBean
@RequestScoped
public class RegisterBean {
    private String name;
    private String password;
    private String email;
    private SessionFactory sessionFactory;

    public RegisterBean() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String register() {
        String errorMessage = validateInput();
        if (errorMessage != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, null));
            return null;
        }

        if (registerUser()) {
            return "login?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed. Please try again.", null));
            return null;
        }
    }

    private boolean registerUser() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            UserEntity user = new UserEntity();
            user.setName(name);
            user.setPassword(password);
            user.setEmail(email);

            session.save(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return false;
    }

    private String validateInput() {
        if (name == null || name.trim().isEmpty() || name.contains(" ")) {
            return "Username cannot be empty or contain spaces.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            return "Invalid email address.";
        }
        return null;
    }
}