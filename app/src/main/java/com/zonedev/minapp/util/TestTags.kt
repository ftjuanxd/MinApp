package com.zonedev.minapp.util

object TestTags {
    const val EMAIL_FIELD = "login_email_field"
    const val PASSWORD_FIELD = "login_password_field"
    const val LOGIN_BUTTON = "login_submit_button"
}
object ObservationsTestTags {
    const val SUBJECT_FIELD = "ObservationSubjectField"
    const val OBSERVATION_FIELD = "ObservationTextField"
    const val SUBMIT_BUTTON = "ObservationSubmitButton"
    const val SUCCESS_MODAL = "ObservationSuccessModal"
    const val ERROR_MODAL = "ObservationErrorModal"
}
object TemplateTestTags {
    const val ID_FIELD = "TemplateIdField"
    const val NAME_FIELD = "TemplateNameField"
    const val DESTINY_FIELD = "TemplateDestinyField"
    const val AUTHORIZATION_FIELD = "TemplateAuthorizationField"
    const val DESCRIPTION_FIELD = "TemplateDescriptionField"
    const val SUBMIT_BUTTON = "TemplateSubmitButton"
    const val SUCCESS_MODAL = "TemplateSuccessModal"
    const val VALIDATION_ERROR_MODAL = "TemplateValidationErrorModal"
}

object ReportViewTestTags {
    const val DROPDOWN_BUTTON = "ReportTypeDropdownButton"
    const val DROPDOWN_MENU = "ReportTypeDropdownMenu"
    fun dropdownItem(type: String) = "DropdownItem_$type" // Función para tags dinámicos

    const val ID_FILTER_FIELD = "IdFilterField"
    const val NAME_FILTER_FIELD = "NameFilterField"
    const val OBSERVATION_TITLE_FILTER_FIELD = "ObservationTitleFilterField"

    const val START_DATE_FIELD = "StartDateField"
    const val END_DATE_FIELD = "EndDateField"
    const val DATE_PICKER_OK_BUTTON = "DatePickerOkButton"
    const val DATE_PICKER_CLEAR_BUTTON = "DatePickerClearButton"

    const val REPORT_LIST_CONTAINER = "ReportListContainer"
    fun reportRow(reportId: String) = "ReportRow_$reportId" // Tag dinámico por ID de reporte
    const val EMPTY_LIST_MESSAGE = "EmptyListMessage"

    const val DETAILS_MODAL = "ReportDetailsModal"
    const val DETAILS_MODAL_CLOSE_BUTTON = "DetailsModalCloseButton"

}
