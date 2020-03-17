/**
 * Copyright (c) 2020 Jalasoft.
 *
 * This software is the confidential and proprietary information of Jalasoft.
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Jalasoft.
 */

package org.jalasoft.moi.controller.endpoints;

import io.swagger.annotations.Api;

import org.jalasoft.moi.controller.services.FileService;
import org.jalasoft.moi.model.core.IHandler;
import org.jalasoft.moi.model.core.Language;
import org.jalasoft.moi.model.core.Params;

import org.jalasoft.moi.model.cplusplus.CppHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * This class defines the controller for C++.
 *
 * @author Diego Perez.
 *         Carlos Meneses.
 * @version 1.1
 */
@RestController
@RequestMapping(path = "/onlineCompiler/cpp")
@Api(value = "cplusplus", description = "Implement compile and run code in C++")
public class CppController {

    @Autowired
    private FileService fileService;
    private static final String FILE_PATH = ".\\temp\\cplusplus\\";
    private static final String EXTENSION = ".cc";
    private Language language = Language.CPP;

    /**
     * Returns a String that shows the output of the program.
     *
     * @return the output from the execution.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/execute")
    public String executeCode(@RequestParam(value = "fileName") String fileName,
                              @RequestParam(value = "code") String code) throws IOException {
        IHandler handler = new CppHandler();
        Params codeParams = fileService.saveFile(fileName, code, FILE_PATH, EXTENSION, language);
        return handler.execute(codeParams);
    }

    /**
     * This method is used to save the changes in a file determined by a name.
     *
     * @return a message of the realized action.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/save")
    public String saveCode(@RequestParam(value = "fileName") String fileName,
                           @RequestParam(value = "code") String code) throws IOException {
        fileService.saveFile(fileName, code, FILE_PATH, EXTENSION, language);
        return "Your code was successfully saved";
    }
}