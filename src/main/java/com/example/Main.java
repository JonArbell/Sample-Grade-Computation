import com.example.Model.GradeComponent;

public void main(String... args){

    try(var scanner = new Scanner(System.in)){

        var gradeComponents = Arrays.asList(
                new GradeComponent("Written Work", "a"),
                new GradeComponent("Performance Task", "b"),
                new GradeComponent("Quarterly Assessment", "c"),
                new GradeComponent("Kasipsipan","d")
        );

        display(gradeComponents);

        System.out.print("Input formula: ");
        var formula = scanner.nextLine();

        var weightMap = parseFormula(formula);

        System.out.println("Input scores:");

        var scores = getScores(gradeComponents, weightMap, scanner);

        var finalGrade = Math.round(scores.values().stream().mapToDouble(Double::doubleValue).sum());

        System.out.printf("Final Grade: %d",finalGrade);
    }catch (NumberFormatException e) {
        System.err.println("Invalid number format: " + e.getMessage());
    } catch (NullPointerException e) {
        System.err.println("Unexpected null value. Check your input or formula.");
    } catch (Exception e) {
        System.err.println("An unexpected error occurred: " + e.getMessage());
    }

}

private Matcher createMatcher(String pattern, String formula){
    return Pattern.compile(pattern).matcher(formula);
}

private Map<String, Double> parseFormula(String formula) {

    var weightMap = new HashMap<String, Double>();

    var specialMatcher = createMatcher("(?<!\\()\\b(\\d+)\\b(?!%)", formula);

    if (specialMatcher.find()){
        var special = Double.parseDouble(specialMatcher.group(1));
        weightMap.put("special", special);
    }

    var matcher = createMatcher("\\((\\w+)\\s*\\*\\s*(\\d+)%\\)", formula);

    while (matcher.find()) {
        double percentToDecimal = Double.parseDouble(matcher.group(2)) / 100;
        weightMap.put(matcher.group(1), percentToDecimal);
    }

    return weightMap;
}

private Map<String, Double> getScores(
        List<GradeComponent> gradeComponents,
        Map<String, Double> weightMap,
        Scanner scanner){

    var mapScores = new HashMap<String, Double>();

    Optional.ofNullable(weightMap.get("special"))
            .ifPresent(specialGrade ->
                    mapScores.put("special", specialGrade));

    gradeComponents.forEach(component ->
            Optional.ofNullable(weightMap.get(component.getKey()))
                    .ifPresent(grade ->
                            mapScores.put(component.getKey(),finalGrade(component, grade, scanner)))

    );

    return mapScores;
}

private double finalGrade(GradeComponent gradeComponent, double grade, Scanner scanner){

    System.out.printf("%s [0 - 100]: ",gradeComponent.getName());
    var score = scanner.nextInt();
    return score * grade;

}

private void display(List<GradeComponent> components){
    System.out.println("-- Sample Grade Computation --");

    System.out.println("Write your own formula.\nWhere:");

    components.forEach(component ->
            System.out.printf("\t%s is for \"%s\" \n", component.getKey(),component.getName()));
}