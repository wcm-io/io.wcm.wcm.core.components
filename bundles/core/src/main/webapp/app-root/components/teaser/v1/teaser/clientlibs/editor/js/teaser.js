/*
  changes from /apps/core/wcm/components/teaser/v1/teaser/clientlibs/editor/js/teaser.js
  - add conditions to check if linkURL really exists in the dialog
*/

/* global jQuery */
(function($) {
    "use strict";

    var dialogContentSelector = ".cmp-teaser__editor";
    var actionsEnabledCheckboxSelector = 'coral-checkbox[name="./actionsEnabled"]';
    var actionsMultifieldSelector = ".cmp-teaser__editor-multifield_actions";
    var titleCheckboxSelector = 'coral-checkbox[name="./titleFromPage"]';
    var titleTextfieldSelector = 'input[name="./jcr:title"]';
    var descriptionCheckboxSelector = 'coral-checkbox[name="./descriptionFromPage"]';
    var descriptionTextfieldSelector = '.cq-RichText-editable[name="./jcr:description"]';
    var linkURLSelector = '[name="./linkURL"]';
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1;
    var actionsEnabled;
    var titleTuple;
    var descriptionTuple;
    var linkURL;

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {

            var rteInstance = $(descriptionTextfieldSelector).data("rteinstance");
            // wait for the description textfield rich text editor to signal start before initializing.
            // Ensures that any state adjustments made here will not be overridden.
            if (rteInstance && rteInstance.isActive) {
                init(e, $dialog, $dialogContent, dialogContent);
            } else {
                $(descriptionTextfieldSelector).on("editing-start", function() {
                    init(e, $dialog, $dialogContent, dialogContent);
                });
            }
        }
    });

    // Initialize all fields once both the dialog and the description textfield RTE have loaded
    function init(e, $dialog, $dialogContent, dialogContent) {
        titleTuple = new CheckboxTextfieldTuple(dialogContent, titleCheckboxSelector, titleTextfieldSelector, false);
        descriptionTuple = new CheckboxTextfieldTuple(dialogContent, descriptionCheckboxSelector, descriptionTextfieldSelector, true);
        retrievePageInfo($dialogContent);

        var $linkURLField = $dialogContent.find(linkURLSelector);
        if ($linkURLField.length) {
            linkURL = $linkURLField.adaptTo("foundation-field").getValue();
            $linkURLField.on("change", function() {
                linkURL = $linkURLField.adaptTo("foundation-field").getValue();
                retrievePageInfo($dialogContent);
            });
        }

        var $actionsEnabledCheckbox = $dialogContent.find(actionsEnabledCheckboxSelector);
        if ($actionsEnabledCheckbox.size() > 0) {
            actionsEnabled = $actionsEnabledCheckbox.adaptTo("foundation-field").getValue() === "true";
            $actionsEnabledCheckbox.on("change", function(e) {
                actionsEnabled = $(e.target).adaptTo("foundation-field").getValue() === "true";
                toggleInputs($dialogContent);
                retrievePageInfo($dialogContent);
            });

            var $actionsMultifield = $dialogContent.find(actionsMultifieldSelector);
            $actionsMultifield.on("change", function(event) {
                var $target = $(event.target);
                if ($target.is("coral-multifield") && event.target.items && event.target.items.length === 0) {
                    actionsEnabled = false;
                    $actionsEnabledCheckbox.adaptTo("foundation-field").setValue(false);
                    toggleInputs($dialogContent);
                } else if ($target.is("foundation-autocomplete")) {
                    updateText($target);
                }
                retrievePageInfo($dialogContent);
            });
        }
        toggleInputs($dialogContent);
    }

    function toggleInputs(dialogContent) {
        var $actionsMultifield = dialogContent.find(actionsMultifieldSelector);
        var linkURLField = dialogContent.find(linkURLSelector).adaptTo("foundation-field");
        var actions = $actionsMultifield.adaptTo("foundation-field");
        if (linkURLField && actions) {
            if (actionsEnabled) {
                linkURLField.setDisabled(true);
                actions.setDisabled(false);
                if ($actionsMultifield.size() > 0) {
                    var actionsMultifield = $actionsMultifield[0];
                    if (actionsMultifield.items.length < 1) {
                        var newMultifieldItem = new Coral.Multifield.Item();
                        actionsMultifield.items.add(newMultifieldItem);
                        Coral.commons.ready(newMultifieldItem, function(element) {
                            var linkField = $(element).find("[data-cmp-teaser-v1-dialog-edit-hook='actionLink']");
                            if (linkField) {
                                linkField.val(linkURL);
                                linkField.trigger("change");
                            }
                        });
                    } else {
                        toggleActionItems($actionsMultifield, false);
                    }
                }
            } else {
                linkURLField.setDisabled(false);
                actions.setDisabled(true);
                toggleActionItems($actionsMultifield, true);
            }
        }
    }

    function toggleActionItems(actionsMultifield, disabled) {
        actionsMultifield.find("coral-multifield-item").each(function(ix, item) {
            var linkField = $(item).find("[data-cmp-teaser-v1-dialog-edit-hook='actionLink']").adaptTo("foundation-field");
            var textField = $(item).find("[data-cmp-teaser-v1-dialog-edit-hook='actionTitle']").adaptTo("foundation-field");
            if (disabled && linkField.getValue() === "" && textField.getValue() === "") {
                actionsMultifield[0].items.remove(item);
            }
            linkField.setDisabled(disabled);
            textField.setDisabled(disabled);
        });
    }

    function retrievePageInfo(dialogContent) {
        var url;
        if (actionsEnabled) {
            url = dialogContent.find('.cmp-teaser__editor-multifield_actions [data-cmp-teaser-v1-dialog-edit-hook="actionLink"]').val();
        } else {
            url = linkURL;
        }
        if (url && url.startsWith("/")) {
            return $.ajax({
                url: url + "/_jcr_content.json"
            }).done(function(data) {
                if (data) {
                    titleTuple.seedTextValue(data["jcr:title"]);
                    titleTuple.update();
                    descriptionTuple.seedTextValue(data["jcr:description"]);
                    descriptionTuple.update();
                }
            });
        } else {
            titleTuple.update();
            descriptionTuple.update();
        }
    }

    function updateText(target) {
        var url = target.val();
        if (url && url.startsWith("/")) {
            var textField = target.parents("coral-multifield-item").find('[data-cmp-teaser-v1-dialog-edit-hook="actionTitle"]');
            if (textField && !textField.val()) {
                $.ajax({
                    url: url + "/_jcr_content.json"
                }).done(function(data) {
                    if (data) {
                        textField.val(data["jcr:title"]);
                    }
                });
            }
        }
    }
})(jQuery);
