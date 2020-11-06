<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>Listing RejectionReasons Items</title>
            <link rel="stylesheet" type="text/css" href="/" />
        </head>
        <body>
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h1>Listing RejectionReasons Items</h1>
            <h:form styleClass="jsfcrud_list_form">
                <h:outputText escape="false" value="(No RejectionReasons Items Found)<br />" rendered="#{rejectionReasons.pagingInfo.itemCount == 0}" />
                <h:panelGroup rendered="#{rejectionReasons.pagingInfo.itemCount > 0}">
                    <h:outputText value="Item #{rejectionReasons.pagingInfo.firstItem + 1}..#{rejectionReasons.pagingInfo.lastItem} of #{rejectionReasons.pagingInfo.itemCount}"/>&nbsp;
                    <h:commandLink action="#{rejectionReasons.prev}" value="Previous #{rejectionReasons.pagingInfo.batchSize}" rendered="#{rejectionReasons.pagingInfo.firstItem >= rejectionReasons.pagingInfo.batchSize}"/>&nbsp;
                    <h:commandLink action="#{rejectionReasons.next}" value="Next #{rejectionReasons.pagingInfo.batchSize}" rendered="#{rejectionReasons.pagingInfo.lastItem + rejectionReasons.pagingInfo.batchSize <= rejectionReasons.pagingInfo.itemCount}"/>&nbsp;
                    <h:commandLink action="#{rejectionReasons.next}" value="Remaining #{rejectionReasons.pagingInfo.itemCount - rejectionReasons.pagingInfo.lastItem}"
                                   rendered="#{rejectionReasons.pagingInfo.lastItem < rejectionReasons.pagingInfo.itemCount && rejectionReasons.pagingInfo.lastItem + rejectionReasons.pagingInfo.batchSize > rejectionReasons.pagingInfo.itemCount}"/>
                    <h:dataTable value="#{rejectionReasons.rejectionReasonsItems}" var="item" border="0" cellpadding="2" cellspacing="0" rowClasses="jsfcrud_odd_row,jsfcrud_even_row" rules="all" style="border:solid 1px">
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Id"/>
                            </f:facet>
                            <h:outputText value="#{item.id}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Reason"/>
                            </f:facet>
                            <h:outputText value="#{item.reason}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Description"/>
                            </f:facet>
                            <h:outputText value="#{item.description}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText escape="false" value="&nbsp;"/>
                            </f:facet>
                            <h:commandLink value="Show" action="#{rejectionReasons.detailSetup}">
                                <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][rejectionReasons.converter].jsfcrud_invoke}"/>
                            </h:commandLink>
                            <h:outputText value=" "/>
                            <h:commandLink value="Edit" action="#{rejectionReasons.editSetup}">
                                <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][rejectionReasons.converter].jsfcrud_invoke}"/>
                            </h:commandLink>
                            <h:outputText value=" "/>
                            <h:commandLink value="Destroy" action="#{rejectionReasons.remove}">
                                <f:param name="jsfcrud.currentRejectionReasons" value="#{jsfcrud_class['com.innotech.xraymanagerapp.controller.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][rejectionReasons.converter].jsfcrud_invoke}"/>
                            </h:commandLink>
                        </h:column>

                    </h:dataTable>
                </h:panelGroup>
                <br />
                <h:commandLink action="#{rejectionReasons.createSetup}" value="New RejectionReasons"/>
                <br />


            </h:form>
        </body>
    </html>
</f:view>
