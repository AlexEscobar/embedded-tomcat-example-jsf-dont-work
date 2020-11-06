<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>RejectionReasons Detail</title>
            <link rel="stylesheet" type="text/css" href="/" />
        </head>
        <body>
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h1>RejectionReasons Detail</h1>
            <h:form>
                <h:panelGrid columns="2">
                    <h:outputText value="Id:"/>
                    <h:outputText value="#{rejectionReasons.rejectionReasons.id}" title="Id" />
                    <h:outputText value="Reason:"/>
                    <h:outputText value="#{rejectionReasons.rejectionReasons.reason}" title="Reason" />
                    <h:outputText value="Description:"/>
                    <h:outputText value="#{rejectionReasons.rejectionReasons.description}" title="Description" />

                    <h:outputText value="ImageRejectionList:" />
                    <h:panelGroup>
                        <h:outputText rendered="#{empty rejectionReasons.rejectionReasons.imageRejectionList}" value="(No Items)"/>
                        <h:dataTable value="#{rejectionReasons.rejectionReasons.imageRejectionList}" var="item" 
                                     border="0" cellpadding="2" cellspacing="0" rowClasses="jsfcrud_odd_row,jsfcrud_even_row" rules="all" style="border:solid 1px" 
                                     rendered="#{not empty rejectionReasons.rejectionReasons.imageRejectionList}">
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="Id"/>
                                </f:facet>
                                <h:outputText value="#{item.id}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="EntryDate"/>
                                </f:facet>
                                <h:outputText value="#{item.entryDate}">
                                    <f:convertDateTime pattern="MM/dd/yyyy HH:mm:ss" />
                                </h:outputText>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="ImageId"/>
                                </f:facet>
                                <h:outputText value="#{item.imageId}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText value="RejectionReasonId"/>
                                </f:facet>
                                <h:outputText value="#{item.rejectionReasonId}"/>
                            </h:column>
                            <h:column>
                                <f:facet name="header">
                                    <h:outputText escape="false" value="&nbsp;"/>
                                </f:facet>
                                <h:commandLink value="Show" action="#{imageRejection.detailSetup}">
                                    <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][rejectionReasons.rejectionReasons][rejectionReasons.converter].jsfcrud_invoke}"/>
                                    <f:param name="jsfcrud.currentImageRejection" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][imageRejection.converter].jsfcrud_invoke}"/>
                                    <f:param name="jsfcrud.relatedController" value="rejectionReasons" />
                                    <f:param name="jsfcrud.relatedControllerType" value="com.innotech.xraymanagerapp.controller.RejectionReasonsController" />
                                </h:commandLink>
                                <h:outputText value=" "/>
                                <h:commandLink value="Edit" action="#{imageRejection.editSetup}">
                                    <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][rejectionReasons.rejectionReasons][rejectionReasons.converter].jsfcrud_invoke}"/>
                                    <f:param name="jsfcrud.currentImageRejection" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][imageRejection.converter].jsfcrud_invoke}"/>
                                    <f:param name="jsfcrud.relatedController" value="rejectionReasons" />
                                    <f:param name="jsfcrud.relatedControllerType" value="com.innotech.xraymanagerapp.controller.RejectionReasonsController" />
                                </h:commandLink>
                                <h:outputText value=" "/>
                                <h:commandLink value="Destroy" action="#{imageRejection.destroy}">
                                    <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][rejectionReasons.rejectionReasons][rejectionReasons.converter].jsfcrud_invoke}"/>
                                    <f:param name="jsfcrud.currentImageRejection" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][imageRejection.converter].jsfcrud_invoke}"/>
                                    <f:param name="jsfcrud.relatedController" value="rejectionReasons" />
                                    <f:param name="jsfcrud.relatedControllerType" value="com.innotech.xraymanagerapp.controller.RejectionReasonsController" />
                                </h:commandLink>
                            </h:column>
                        </h:dataTable>
                    </h:panelGroup>

                </h:panelGrid>
                <br />
                <h:commandLink action="#{rejectionReasons.remove}" value="Destroy">
                    <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][rejectionReasons.rejectionReasons][rejectionReasons.converter].jsfcrud_invoke}" />
                </h:commandLink>
                <br />
                <br />
                <h:commandLink action="#{rejectionReasons.editSetup}" value="Edit">
                    <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][rejectionReasons.rejectionReasons][rejectionReasons.converter].jsfcrud_invoke}" />
                </h:commandLink>
                <br />
                <h:commandLink action="#{rejectionReasons.createSetup}" value="New RejectionReasons" />
                <br />
                <h:commandLink action="#{rejectionReasons.listSetup}" value="Show All RejectionReasons Items"/>
                <br />

            </h:form>
        </body>
    </html>
</f:view>
