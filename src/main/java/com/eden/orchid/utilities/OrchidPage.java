package com.eden.orchid.utilities;

import com.eden.orchid.Orchid;
import com.eden.orchid.utilities.resources.OrchidResource;
import com.eden.orchid.utilities.resources.OrchidResources;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class OrchidPage {

    private OrchidPage next;
    private OrchidPage previous;

    private String title;
    private String description;
    private String url;
    private String alias;

    private String fileName;
    private String filePath;

    private JSONObject data;
    private JSONArray menu;

    private OrchidResource resource;

    public OrchidPage(OrchidResource resource) {
        this.resource = resource;

        if(!OrchidUtils.isEmpty(resource.queryData("title"))) {
            this.title = resource.queryData("title").toString();
        }
        else {
            this.title = FilenameUtils.removeExtension(resource.getFileName());
        }

        if(!OrchidUtils.isEmpty(resource.queryData("description"))) {
            this.description = resource.queryData("description").toString();
        }

        if(!OrchidUtils.isEmpty(resource.queryData("url"))) {
            this.url = resource.queryData("url").toString();
        }
        else {
            this.url = resource.getPath()
                    + File.separator
                    + FilenameUtils.removeExtension(resource.getFileName())
                    + "."
                    + Orchid.getTheme().getOutputExtension(FilenameUtils.getExtension(resource.getFileName()));
        }

        this.fileName = resource.getFileName();
        this.filePath = resource.getPath();

        if(resource.getData().getElement() instanceof JSONObject) {
            this.data = (JSONObject) resource.getData().getElement();
        }
        else {
            this.data = new JSONObject();
            this.data.put("data", resource.getData().getElement());
        }

        if(resource.queryData("menu") != null) {
            if(resource.queryData("menu").getElement() instanceof JSONArray) {
                this.menu = (JSONArray) resource.queryData("menu").getElement();
            }
            else {
                this.menu = new JSONArray();
                this.menu.put(resource.queryData("menu").getElement());
            }
        }
    }

    public void renderTemplate(String templateName) {
        render(templateName, FilenameUtils.getExtension(templateName), true);
    }

    public void renderString(String template, String extension) {
        render(template, extension, false);
    }

    private boolean render(String template, String extension, boolean templateReference) {
        String templateContent = "";
        if(templateReference) {
            OrchidResource templateResource = OrchidResources.getResourceEntry(template);

            if(templateResource == null) {
                templateResource = OrchidResources.getResourceEntry("templates/pages/index.twig");
            }
            if(templateResource == null) {
                templateResource = OrchidResources.getResourceEntry("templates/pages/index.html");
            }
            if(templateResource == null) {
                return false;
            }
            
            templateContent = templateResource.getContent();
        }
        else {
            templateContent = (OrchidUtils.isEmpty(template)) ? "" : template;
        }

        JSONObject pageData = buildPageData();

        JSONObject templateVariables = new JSONObject(Orchid.getRoot().toMap());
        templateVariables.put("page", pageData);
        if(!OrchidUtils.isEmpty(alias)) {
            templateVariables.put(alias, pageData);
        }

        String content = Orchid.getTheme().compile(extension, templateContent, templateVariables);
        OrchidResources.writeFile(filePath, fileName, content);

        return true;
    }

    private JSONObject buildPageData() {
        JSONObject pageData = new JSONObject();
        if(data != null) {
            for(String key : data.keySet()) {
                pageData.put(key, data.get(key));
            }
        }
        if(menu != null) {
            pageData.put("menu", menu);
        }

        JSONObject previousData = buildLink(previous);
        if(previousData != null) {
            pageData.put("previous", previousData);
        }
        JSONObject nextData = buildLink(next);
        if(nextData != null) {
            pageData.put("next", nextData);
        }

        if(!OrchidUtils.isEmpty(title)) {
            pageData.put("title", title);
        }

        if(!OrchidUtils.isEmpty(description)) {
            pageData.put("description", description);
        }

        if(!OrchidUtils.isEmpty(resource.getContent())) {
            pageData.put("content",
                    Orchid.getTheme().compile(
                            FilenameUtils.getExtension(resource.getFileName()),
                            resource.getContent()
                    ));
        }

        return pageData;
    }

    private JSONObject buildLink(OrchidPage page) {
        if(page != null) {
            JSONObject pageData = new JSONObject();

            pageData.put("title", page.getTitle());
            pageData.put("description", page.getDescription());
            pageData.put("url", page.getUrl());

            return pageData;
        }

        return null;
    }

// Getters and Setters
//----------------------------------------------------------------------------------------------------------------------
    public OrchidPage getNext() {
        return next;
    }

    public void setNext(OrchidPage next) {
        this.next = next;
    }

    public OrchidPage getPrevious() {
        return previous;
    }

    public void setPrevious(OrchidPage previous) {
        this.previous = previous;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        String baseUrl = "";
        if(Orchid.query("options.baseUrl") != null) {
            baseUrl = Orchid.query("options.baseUrl").toString();
        }
        if(!url.startsWith(baseUrl)) {
            this.url = baseUrl + File.separator + baseUrl;
        }
        else {
            this.url = url;
        }
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public JSONArray getMenu() {
        return menu;
    }

    public void setMenu(JSONArray menu) {
        this.menu = menu;
    }

    public void usePrettyUrl() {
        filePath = filePath + File.separator + FilenameUtils.removeExtension(fileName);
        fileName = "index." + Orchid.getTheme().getOutputExtension(FilenameUtils.getExtension(fileName));
        setUrl(filePath + File.separator + fileName);
    }
}
