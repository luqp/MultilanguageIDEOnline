/**
 * Copyright (c) 2020 Jalasoft.
 *
 * This software is the confidential and proprietary information of Jalasoft.
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Jalasoft.
 */

package org.jalasoft.moi.controller.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.jalasoft.moi.controller.repository.FileRepository;
import org.jalasoft.moi.controller.repository.ProjectRepository;
import org.jalasoft.moi.domain.FileCode;
import org.jalasoft.moi.domain.Project;
import org.jalasoft.moi.model.core.parameters.Parameters;
import org.jalasoft.moi.model.core.parameters.Params;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manages actions that each file will need as save file or show responses.
 *
 * @author Carlos Meneses
 * @version 1.2
 */
@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Gets a list of all files.
     *
     * @return a list of files
     */
    public Iterable<FileCode> getAllFiles() {
        return fileRepository.findAll();
    }

    /**
     * Gets a file by a id parameter.
     *
     * @param id to search a specific file
     * @return contains a file found by id
     */
    public FileCode getFileById(Long id) {
        return fileRepository.findById(id).get();
    }

    /**
     * Inserts a new file in to data base.
     *
     * @param name inserts the new project name
     * @param code inserts the new project description
     * @param projectID inserts the new project language
     * @return contains the inserted project information
     */
    public FileCode addNewFile(String name, String code, Long projectID) throws IOException {
        FileCode newFilecode = new FileCode();
        newFilecode.setName(name);
        newFilecode.setCode(saveFile(name,code, projectID));
        newFilecode.setProject(projectRepository.findById(projectID).get());
        return fileRepository.save(newFilecode);
    }

    /**
     * Updates a file information in the data base.
     *
     * @param id finds a file to be updated
     * @param name updates the file name field
     * @param code updates the code field
     * @return contains the updated project information
     */
    public FileCode updateFileInfo(Long id, String name, String code) throws IOException {
        FileCode fileToUpdate = fileRepository.findById(id).get();
        fileToUpdate.setName(name);
        fileToUpdate.setCode(updateFile(id, name, code));
        return fileRepository.save(fileToUpdate);
    }

    /**
     * Deletes a file by the parameter id.
     *
     * @param id to search for the file to delete
     */
    public void deleteFileInfo(Long id) throws IOException {
        deleteFile(id);
        fileRepository.deleteById(id);
    }

    /**
     * Creates a new file with name and code to set the file properties in base 64.
     *
     * @param name saves the file name in the project
     * @param codeB64 saves the code inside the file in base 64
     * @param projectId gets the project path by project id
     * @return a parameters object
     */
    public Parameters saveFileB64(String name, String codeB64, Long projectId) throws IOException {
        byte[] byteArray = Base64.decodeBase64(codeB64.getBytes());
        String code = new String(byteArray);
        saveFile(name, code, projectId);
        return setParams(projectId);
    }

    /**
     * Create a new file with name and code to set the file properties in a new params object.
     *
     * @param name saves the file name in the project path
     * @param code saves the code inside the file
     * @param projectId gets the project path by project id
     */
    private String saveFile(String name, String code, Long projectId) throws IOException {
        Project projInfo = projectRepository.findById(projectId).get();
        String filePath = projInfo.getPath()+"/"+ name + projInfo.getLanguage().getFileExtention();
        FileWriter codeWriter = new FileWriter(filePath);
        codeWriter.write(code);
        codeWriter.close();
        return filePath;
    }

    /**
     * Updates a new file with name and code to set the file properties in base 64.
     *
     * @param fileId gets the file information by its id
     * @param name updates the file name in the project
     * @param codeB64 updates the code inside the file in base 64
     * @return a parameters object
     */
    public void updateFileB64(Long fileId, String name, String codeB64) throws IOException {
        byte[] byteArray = Base64.decodeBase64(codeB64.getBytes());
        String code = new String(byteArray);
        updateFile(fileId, name, code);
    }

    /**
     * Updates a new file with name and code to set the file properties in a new params object.
     *
     * @param fileId gets the file information by its id
     * @param name updates the file name in the project path
     * @param code updates the code inside the file
     */
    private String updateFile(Long fileId, String name, String code) throws IOException {
        deleteFile(fileId);
        FileCode fileCode = fileRepository.findById(fileId).get();
        Project projInfo = fileCode.getProject();
        String filePath = projInfo.getPath()+"/"+ name + projInfo.getLanguage().getFileExtention();
        FileWriter codeWriter = new FileWriter(filePath);
        codeWriter.write(code);
        codeWriter.close();
        return filePath;
    }

    /**
     * Deletes a file from the project foler.
     *
     * @param fileId gets a file information by its id
     */
    private void deleteFile(Long fileId) throws IOException {
        FileCode fileCode = fileRepository.findById(fileId).get();
        Project projInfo = fileCode.getProject();
        String name = fileCode.getName();
        String filePath = projInfo.getPath()+"/"+ name + projInfo.getLanguage().getFileExtention();
        File file = new File(filePath);
        file.delete();
    }

    /**
     * Create a new file with name and code to set the file properties in a new params object.
     *
     * @param projectId gets the project path by project id
     * @return the parameters setted object
     */
    public Parameters setParams(Long projectId){
        Project projInfo = projectRepository.findById(projectId).get();
        Parameters codeParams = new Params();
        File codeFile = new File(projInfo.getPath());
        codeParams.setFilesPath(codeFile.toPath());
        codeParams.setLanguage(projInfo.getLanguage());
        return codeParams;
    }
}
