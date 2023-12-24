package zxc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CourseRegistrationModel {

    enum TeacherType {
        PROFESSOR, DOCTOR, TRAINEE, ACADEMIC, PRACTITIONER
    }

    enum ManagerType {
        ACADEM_MANAGER, OR_MANAGER
    }

    class Lesson {
    }

    class Course {
        private String courseName;
        private int yearOfStudy;

        public Course(String courseName, int yearOfStudy) {
            this.courseName = courseName;
            this.yearOfStudy = yearOfStudy;
        }

        public String getCourseName() {
            return courseName;
        }

        public int getYearOfStudy() {
            return yearOfStudy;
        }
    }

    interface CanViewAcademicInfo {
        void viewAcademicInfo();
    }

    interface Observer {
        void update(String message);
    }

    abstract class Manager implements CanViewAcademicInfo, Observer {
        private String name;
        private ManagerType type;

        public Manager(String name, ManagerType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public ManagerType getType() {
            return type;
        }

        public abstract void approveRegistration(Object courses, Student student, boolean intentionToAttend);

        @Override
        public void update(String message) {
            System.out.println(getType() + " " + getName() + ": " + message);
        }
    }

    class AcademManager extends Manager implements CanViewAcademicInfo {
        public AcademManager(String name) {
            super(name, ManagerType.ACADEM_MANAGER);
        }

        @Override
        public void viewAcademicInfo() {
        }

        @Override
        public void approveRegistration(Object courses, Student student, boolean intentionToAttend) {
            update("Access denied. Academ Managers cannot approve registrations.");
        }
    }

    public class ORManager extends Manager implements CanViewAcademicInfo {
        private List<Course> courses = new ArrayList<>();
        private List<Teacher> teachers = new ArrayList<>();
        private List<Student> students = new ArrayList<>();

        public ORManager(String name) {
            super(name, ManagerType.OR_MANAGER);
        }

        public void addRegistrationCourse(Faculty faculty, String courseName, int yearOfStudy) {
            Course newCourse = new Course(courseName, yearOfStudy);
            courses.add(newCourse);
        }

        public void addTeacher(String name, TeacherType type) {
            Teacher newTeacher = new Teacher(name, type);
            teachers.add(newTeacher);
        }

        public void addStudent(String name, String major) {
            Student newStudent = new Student(name, major);
            students.add(newStudent);
        }

        public void associateStudentWithCourse(Student student, Course course) {
            student.enrollCourse(course);
        }

        public void associateTeacherWithCourse(Teacher teacher, Course course) {
            teacher.addLessonToSchedule(new Lesson());
        }

        public void approveRegistration(Object courses, Student student, boolean intentionToAttend) {
            if (getType() == ManagerType.OR_MANAGER) {
                if (courses instanceof Course) {
                    Course singleCourse = (Course) courses;
                    if (intentionToAttend) {
                        associateStudentWithCourse(student, singleCourse);
                        update("Student " + student.getName() + " enrolled in course " + singleCourse.getCourseName());
                    } else {
                        update("Student " + student.getName() + " does not have the intention to attend the course.");
                    }
                } else if (courses instanceof List<?>) {
                    List<?> courseList = (List<?>) courses;
                    for (Object obj : courseList) {
                        if (obj instanceof Course) {
                            Course course = (Course) obj;
                            if (intentionToAttend) {
                                associateStudentWithCourse(student, course);
                                update("Student " + student.getName() + " enrolled in course " + course.getCourseName());
                            } else {
                                update("Student " + student.getName() + " does not have the intention to attend the course: " + course.getCourseName());
                            }
                        } else {
                            update("Invalid course object found in the list");
                        }
                    }
                }
            } else {
                update("Access denied. Only OR Managers can approve registrations.");
            }
        }

        @Override
        public void viewAcademicInfo() {
            // Implementation for viewing academic info
        }

        private Scanner scanner = new Scanner(System.in);

        public void addUserInputCourse() {
            System.out.print("Number of courses: ");
            int numCourses = scanner.nextInt();
            scanner.nextLine();

            for (int i = 0; i < numCourses; i++) {
                System.out.print("Faculty (ECONOMY/EDUCATION/IT/HEALTH/ENGINEERING): ");
                Faculty faculty = null;
                try {
                    String facultyInput = scanner.nextLine().toUpperCase();
                    if (Faculty.isValidFaculty(facultyInput)) {
                        faculty = Faculty.valueOf(facultyInput);
                    } else {
                        throw new IllegalArgumentException("Invalid faculty input. Please enter one of: ECONOMY, EDUCATION, IT, HEALTH, ENGINEERING.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.exit(0);
                }

                System.out.print("Course Name: ");
                String courseName = scanner.nextLine();
                System.out.print("Year of Study: ");
                int yearOfStudy = Integer.parseInt(scanner.nextLine());

                addRegistrationCourse(faculty, courseName, yearOfStudy);
            }
        }

        public void addUserInputStudent() {
            System.out.print("Number of students: ");
            int numStudents = scanner.nextInt();
            scanner.nextLine();

            for (int i = 0; i < numStudents; i++) {
                System.out.print("Student Name: ");
                String name = scanner.nextLine();
                System.out.print("Major: ");
                String major = scanner.nextLine();

                addStudent(name, major);
            }
        }

        public void addUserInputTeacher() {
            System.out.print("Number of teachers: ");
            int numTeachers = scanner.nextInt();
            scanner.nextLine();
            for (int i = 0; i < numTeachers; i++) {
                System.out.print("Teacher Name: ");
                String name = scanner.nextLine();
                System.out.print("Teacher Type (PROFESSOR/DOCTOR/TRAINEE/ACADEMIC/PRACTITIONER): ");
                TeacherType type = TeacherType.valueOf(scanner.nextLine().toUpperCase());

                addTeacher(name, type);
            }
        }

        public void closeScanner() {
            scanner.close();
        }

        public List<Student> getStudents() {
            return students;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public List<Teacher> getTeachers() {
            return teachers;
        }
    }
    class Student {
        private List<Course> courses = new ArrayList<>();
        private String name;
        private String major;

        public void enrollCourse(Course course) {
            courses.add(course);
        }

        public String getName() {
            return name;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public Student(String name, String major) {
            this.name = name;
            this.major = major;
        }

        public void registerForCourses(Object courses, ORManager manager, boolean intentionToAttend) {
            manager.approveRegistration(courses, this, intentionToAttend);
        }

        public void displayCourses() {
            System.out.print("Student name: " + getName() + " (" + major + ", Courses: ");

            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                System.out.print(course.getCourseName() + " - Year " + course.getYearOfStudy());
                if (i < courses.size() - 1) {
                    System.out.print(", ");
                }
            }

            System.out.println(")");
        }
    }


    class Teacher {
        private String name;
        private TeacherType type;
        private List<Lesson> lessons = new ArrayList<>();

        public Teacher(String name, TeacherType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public TeacherType getType() {
            return type;
        }

        public void addLessonToSchedule(Lesson lesson) {
            lessons.add(lesson);
        }

        public void displayType() {
            System.out.println("Teacher: " + getName() + ", Type: " + getType());
        }
    }

    public enum Faculty {
        ECONOMY, EDUCATION, IT, HEALTH, ENGINEERING;

        public static boolean isValidFaculty(String facultyInput) {
            for (Faculty faculty : values()) {
                if (faculty.name().equals(facultyInput)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) {
        CourseRegistrationModel courseRegistrationModel = new CourseRegistrationModel();
        ORManager orManager = courseRegistrationModel.new ORManager("OR Manager");

        // Add courses
        orManager.addUserInputCourse();

        // Add students
        orManager.addUserInputStudent();

        // Add teachers
        orManager.addUserInputTeacher();

        System.out.println("\nStudents:");
        for (Student student : orManager.getStudents()) {
            student.displayCourses();
        }

        System.out.println("\nTeachers:");
        for (Teacher teacher : orManager.getTeachers()) {
            teacher.displayType();
        }

        orManager.closeScanner();
    }
}
