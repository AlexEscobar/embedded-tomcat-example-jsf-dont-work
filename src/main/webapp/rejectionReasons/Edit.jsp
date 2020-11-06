<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>Editing RejectionReasons</title>
            <link rel="stylesheet" type="text/css" href="/" />
        </head>
        <body>
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h1>Editing RejectionReasons</h1>
            <h:form>
                <h:panelGrid columns="2">
                    <h:outputText value="Id:"/>
                    <h:outputText value="#{rejectionReasons.rejectionReasons.id}" title="Id" />
                    <h:outputText value="Reason:"/>
                    <h:inputText id="reason" value="#{rejectionReasons.rejectionReasons.reason}" title="Reason" required="true" requiredMessage="The reason field is required." />
                    <h:outputText value="Description:"/>
                    <h:inputText id="description" value="#{rejectionReasons.rejectionReasons.description}" title="Description" />
                    <h:outputText value="ImageRejectionList:"/>
                    <h:selectManyListbox id="imageRejectionList" value="#{rejectionReasons.rejectionReasons.jsfcrud_transform[jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method.collectionToArray][jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method.arrayToList].imageRejectionList}" title="ImageRejectionList" size="6" converter="#{imageRejection.converter}" >
                        <f:selectItems value="#{imageRejection.imageRejectionItemsAvailableSelectMany}"/>
                    </h:selectManyListbox>

                </h:panelGrid>
                <br />
                <h:commandLink action="#{rejectionReasons.edit}" value="Save">
                    <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][rejectionReasons.rejectionReasons][rejectionReasons.converter].jsfcrud_invoke}"/>
                </h:commandLink>
                <br />
                <br />
                <h:commandLink action="#{rejectionReasons.detailSetup}" value="Show" immediate="true">
                    <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][rejectionReasons.rejectionReasons][rejectionReasons.converter].jsfcrud_invoke}"/>
                </h:commandLink>
                <br />
                <h:commandLink action="#{rejectionReasons.listSetup}" value="Show All RejectionReasons Items" immediate="true"/>
                <br />

            </h:form>
        </body>
    </html>
</f:view>