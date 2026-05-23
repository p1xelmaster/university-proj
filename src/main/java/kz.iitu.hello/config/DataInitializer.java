package kz.iitu.hello.config;

import kz.iitu.hello.domain.entity.PanMaratCourse;
import kz.iitu.hello.domain.entity.PanMaratStudent;
import kz.iitu.hello.domain.entity.PanMaratTeacher;
import kz.iitu.hello.domain.entity.PanMaratUser;
import kz.iitu.hello.domain.enums.PanMaratDepartment;
import kz.iitu.hello.domain.enums.PanMaratUserRole;
import kz.iitu.hello.domain.repository.PanMaratCoursesRepository;
import kz.iitu.hello.domain.repository.PanMaratStudentsRepository;
import kz.iitu.hello.domain.repository.PanMaratTeachersRepository;
import kz.iitu.hello.domain.repository.PanMaratUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PanMaratUsersRepository usersRepository;
    private final PanMaratStudentsRepository studentsRepository;
    private final PanMaratTeachersRepository teachersRepository;
    private final PanMaratCoursesRepository coursesRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        ensureLabAdmin();

        if (usersRepository.count() > 1) {
            return; // только если нет записи
        }

        String encoded = passwordEncoder.encode("test1234");

        // ── Admin ─────────────────────────
        PanMaratUser adminUser = new PanMaratUser();
        adminUser.setUserName("test_admin");
        adminUser.setEmail("test_admin@test.com");
        adminUser.setPassword(encoded);
        adminUser.setRole(PanMaratUserRole.ADMIN);
        usersRepository.save(adminUser);

        // ── all ───────────────────────────
        PanMaratDepartment[] departments = {PanMaratDepartment.IT, PanMaratDepartment.MATHEMATICS, PanMaratDepartment.PHYSICS};
        int[] experiences = {10, 7, 5};
        PanMaratTeacher[] teachers = new PanMaratTeacher[3];

        for (int i = 1; i <= 3; i++) {
            PanMaratUser user = new PanMaratUser();
            user.setUserName("test_teacher_" + i);
            user.setEmail("test_teacher_" + i + "@test.com");
            user.setPassword(encoded);
            user.setRole(PanMaratUserRole.TEACHER);
            usersRepository.save(user);

            PanMaratTeacher teacher = new PanMaratTeacher();
            teacher.setTeacherName("Test PanMaratTeacher " + i);
            teacher.setExperienceYears(experiences[i - 1]);
            teacher.setDepartment(departments[i - 1]);
            teacher.setUser(user);
            teachers[i - 1] = teachersRepository.save(teacher);
        }

        PanMaratCourse course1 = new PanMaratCourse();
        course1.setCourseName("Test PanMaratCourse 1");
        course1.setCredits(4);
        course1.setMaxStudents(30);
        course1.setTeacher(teachers[0]);
        course1 = coursesRepository.save(course1);

        PanMaratCourse course2 = new PanMaratCourse();
        course2.setCourseName("Test PanMaratCourse 2");
        course2.setCredits(3);
        course2.setMaxStudents(30);
        course2.setTeacher(teachers[0]);
        course2 = coursesRepository.save(course2);

        double[] gpas = {3.8, 3.5, 3.2, 3.9, 2.7, 3.1, 2.9, 3.6, 3.3};
        int[] ages    = { 20,  21,  19,  22,  20,  21,  19,  23,  20};

        for (int i = 1; i <= 9; i++) {
            PanMaratUser user = new PanMaratUser();
            user.setUserName("test_student_" + i);
            user.setEmail("test_student_" + i + "@test.com");
            user.setPassword(encoded);
            user.setRole(PanMaratUserRole.STUDENT);
            usersRepository.save(user);

            String group = "test_group_" + ((i - 1) / 3 + 1);

            PanMaratStudent student = new PanMaratStudent();
            student.setStudentName("Test PanMaratStudent " + i);
            student.setAge(ages[i - 1]);
            student.setGroupName(group);
            student.setGpa(gpas[i - 1]);
            student.setUser(user);

            if (i <= 6) student.getCourses().add(course1);
            if (i >= 4) student.getCourses().add(course2);

            studentsRepository.save(student);
        }
    }

    private void ensureLabAdmin() {
        if (usersRepository.existsByUserNameIgnoreCase("lab_admin")) {
            return;
        }
        PanMaratUser admin = new PanMaratUser();
        admin.setUserName("lab_admin");
        admin.setEmail("lab_admin@test.com");
        admin.setPassword(passwordEncoder.encode("test1234"));
        admin.setRole(PanMaratUserRole.ADMIN);
        usersRepository.save(admin);
    }
}
