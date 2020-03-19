package org.jalasoft.moi.model.interaction;

import org.jalasoft.moi.model.core.Executer;
import org.jalasoft.moi.model.core.ICommandBuilder;
import org.jalasoft.moi.model.core.Language;
import org.jalasoft.moi.model.core.parameters.Answer;
import org.jalasoft.moi.model.core.parameters.InputParameters;
import org.jalasoft.moi.model.core.parameters.Params;
import org.jalasoft.moi.model.core.parameters.ProcessResult;
import org.jalasoft.moi.model.core.parameters.Result;
import org.jalasoft.moi.model.utils.Constant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HandleInteractionTest {

    private static ProcessCacheTest processCache;
    private static HashMap<Long, List<String>> map;

    @BeforeAll
    static void initAll() {
        processCache = new ProcessCacheTest();
        map = new HashMap<>();
    }

    @ParameterizedTest
    @MethodSource("codeProvider")
    @Order(1)
    public void executeProcessForTwoDigitsSumTest(Path path, Language language) {
        String expected = "Insert number1\r\n> ";

        Params params = new Params();
        params.setLanguage(language);
        params.setFilesPath(path);

        Result result = createExecution(params);
        assertEquals(expected, result.getValue());

        List<String> numbers = new ArrayList<>();
        numbers.add("4");
        numbers.add("6");
        numbers.add("10");

        map.put(result.getPid(), numbers);
    }


    @ParameterizedTest
    @MethodSource("pidProvider")
    @Order(2)
    public void firstInsertDataToExecutedProcessTest(Long pid) {
        String expected = "Insert number2\r\n> ";
        String number1 = map.get(pid).get(0);

        InputParameters input = new Answer();
        input.setProcessPid(pid);
        input.setValue(number1);

        Result result = buildResultWithInput(input);
        assertEquals(expected, result.getValue());
    }

    @ParameterizedTest
    @MethodSource("pidProvider")
    @Order(3)
    public void secondInsertDataToExecutedProcessTest(Long pid) {
        String number2 = map.get(pid).get(1);
        String sum = map.get(pid).get(2);
        String expected = "Sum: " + sum + "\r\n";

        InputParameters input = new Answer();
        input.setProcessPid(pid);
        input.setValue(number2);

        Result result = buildResultWithInput(input);
        assertEquals(expected, result.getValue());
    }

    static Stream<Arguments> codeProvider() {
        return Stream.of(
                arguments(
                        Constant.ROOTPATH.getValue() + "\\thirdparty\\python\\local\\SumInputsTest.py",
                        Language.PYTHON_32
                ),
                arguments(
                        Constant.ROOTPATH.getValue() + "\\thirdparty\\java\\local\\SumInputsTest.java",
                        Language.JAVA
                )
        );
    }

    static Stream<Long> pidProvider() {
        return processCache.getKeys().stream();
    }

    public Result createExecution(Params params) {
        ICommandBuilder commandBuilder = params.getLanguage().getCommandBuilder();
        String command = commandBuilder.buildCommand(params.getFilesPath());
        Executer executer = new Executer(processCache);
        Result result;
        try {
            result = executer.execute(command);
        } catch (IOException e) {
            e.printStackTrace();
            result = new ProcessResult();
            result.setValue(e.getMessage());
            result.setPid(0);
        }
        return result;
    }

    public Result buildResultWithInput(InputParameters input) {
        Executer executer = new Executer(processCache);
        Result result;
        try {
            result = executer.processAnswer(input);
        } catch (IOException e) {
            e.printStackTrace();
            result = new ProcessResult();
            result.setValue(e.getMessage());
            result.setPid(0);
        }

        return result;
    }
}