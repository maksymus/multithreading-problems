package bathroom;

class Person {
    public enum Gender {
        M, F;

        public Gender getOpposite() {
            switch (this) {
                case F: return M;
                case M: return F;
            }

            return null;
        }
    }

    private String name;
    private Gender gender;

    public Person(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", gender=" + gender +
                '}';
    }
}
