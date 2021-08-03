/*
  changes from /apps/core/wcm/components/image/v2/image/clientlibs/editor/js/image.js
  - add conditions to check if $linkURLGroup and $linkURLField really exists in the dialog
*/

/* global jQuery, Coral */
(function($) {
    "use strict";

    var dialogContentSelector = ".cmp-image__editor";
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1;
    var isDecorative;
    var altTuple;
    var captionTuple;
    var $altGroup;
    var $linkURLGroup;
    var $linkURLField;
    var $cqFileUpload;
    var $cqFileUploadEdit;
    var $dynamicMediaGroup;
    var areDMFeaturesEnabled;
    var fileReference;
    var presetTypeSelector = ".cmp-image__editor-dynamicmedia-presettype";
    var imagePresetDropDownSelector = ".cmp-image__editor-dynamicmedia-imagepreset";
    var smartCropRenditionDropDownSelector = ".cmp-image__editor-dynamicmedia-smartcroprendition";
    var imagePropertiesRequest;
    var imagePath;
    var smartCropRenditionFromJcr;
    var smartCropRenditionsDropDown;

    $(document).on("dialog-loaded", function(e) {
        var $dialog        = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent  = $dialogContent.length > 0 ? $dialogContent[0] : undefined;
        if (dialogContent) {
            isDecorative = dialogContent.querySelector('coral-checkbox[name="./isDecorative"]');
            altTuple = new CheckboxTextfieldTuple(dialogContent, 'coral-checkbox[name="./altValueFromDAM"]', 'input[name="./alt"]');
            $altGroup = $dialogContent.find(".cmp-image__editor-alt");
            $linkURLGroup = $dialogContent.find(".cmp-image__editor-link");
            if ($linkURLGroup) {
              $linkURLField = $linkURLGroup.find('foundation-autocomplete[name="./linkURL"]');
            }
            captionTuple = new CheckboxTextfieldTuple(dialogContent, 'coral-checkbox[name="./titleValueFromDAM"]', 'input[name="./jcr:title"]');
            $cqFileUpload = $dialog.find(".cq-FileUpload");
            $cqFileUploadEdit = $dialog.find(".cq-FileUpload-edit");
            $dynamicMediaGroup = $dialogContent.find(".cmp-image__editor-dynamicmedia");
            $dynamicMediaGroup.hide();
            areDMFeaturesEnabled = ($dynamicMediaGroup.length === 1);
            if (areDMFeaturesEnabled) {
                smartCropRenditionsDropDown = $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).get(0);
            }

            if ($cqFileUpload.length) {
                imagePath = $cqFileUpload.data("cqFileuploadTemporaryfilepath").slice(0, $cqFileUpload.data("cqFileuploadTemporaryfilepath").lastIndexOf("/"));
                retrieveInstanceInfo(imagePath);
                $cqFileUpload.on("assetselected", function(e) {
                    fileReference = e.path;
                    retrieveDAMInfo(fileReference).then(
                        function() {
                            if (isDecorative) {
                                altTuple.hideCheckbox(isDecorative.checked);
                            }
                            captionTuple.hideCheckbox(false);
                            altTuple.reinitCheckbox();
                            captionTuple.reinitCheckbox();
                            toggleAlternativeFieldsAndLink(isDecorative);
                            if (areDMFeaturesEnabled) {
                                selectPresetType($(presetTypeSelector), "imagePreset");
                                resetSelectField($dynamicMediaGroup.find(smartCropRenditionDropDownSelector));
                            }
                        }
                    );
                });
                $cqFileUpload.on("click", "[coral-fileupload-clear]", function() {
                    altTuple.reset();
                    captionTuple.reset();
                });
                $cqFileUpload.on("coral-fileupload:fileadded", function() {
                    if (isDecorative) {
                        altTuple.hideTextfield(isDecorative.checked);
                    }
                    altTuple.hideCheckbox(true);
                    captionTuple.hideTextfield(false);
                    captionTuple.hideCheckbox(true);
                    fileReference = undefined;
                });
            }
            if ($cqFileUploadEdit) {
                fileReference = $cqFileUploadEdit.data("cqFileuploadFilereference");
                if (fileReference === "") {
                    fileReference = undefined;
                }
                if (fileReference) {
                    retrieveDAMInfo(fileReference);
                } else {
                    altTuple.hideCheckbox(true);
                    captionTuple.hideCheckbox(true);
                }
            }
            toggleAlternativeFieldsAndLink(isDecorative);
        }

        $(window).adaptTo("foundation-registry").register("foundation.validation.selector", {
            submittable: ".cmp-image__editor-alt-text",
            candidate: ".cmp-image__editor-alt-text:not(:hidden)",
            exclusion: ".cmp-image__editor-alt-text *"
        });
    });

    $(window).on("focus", function() {
        if (fileReference) {
            retrieveDAMInfo(fileReference);
        }
    });

    $(document).on("dialog-beforeclose", function() {
        $(window).off("focus");
    });

    $(document).on("change", dialogContentSelector + ' coral-checkbox[name="./isDecorative"]', function(e) {
        toggleAlternativeFieldsAndLink(e.target);
    });

    $(document).on("change", dialogContentSelector + " " + presetTypeSelector, function(e) {
        switch (e.target.value) {
            case "imagePreset":
                $dynamicMediaGroup.find(imagePresetDropDownSelector).parent().show();
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().hide();
                resetSelectField($dynamicMediaGroup.find(smartCropRenditionDropDownSelector));
                break;
            case "smartCrop":
                $dynamicMediaGroup.find(imagePresetDropDownSelector).parent().hide();
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().show();
                resetSelectField($dynamicMediaGroup.find(imagePresetDropDownSelector));
                break;
            default:
                break;
        }
    });

    function toggleAlternativeFieldsAndLink(checkbox) {
        if (checkbox) {
            if (checkbox.checked) {
                if ($linkURLGroup) {
                  $linkURLGroup.hide();
                }
                $altGroup.hide();
            } else {
                $altGroup.show();
                if ($linkURLGroup) {
                  $linkURLGroup.show();
                }
            }
            if ($linkURLField) {
              if ($linkURLField.length) {
                  $linkURLField.adaptTo("foundation-field").setDisabled(checkbox.checked);
              }
            }
            altTuple.hideTextfield(checkbox.checked);
            if (fileReference) {
                altTuple.hideCheckbox(checkbox.checked);
            }
        }
    }

    function retrieveDAMInfo(fileReference) {
        return $.ajax({
            url: fileReference + "/_jcr_content/metadata.json"
        }).done(function(data) {
            if (data) {
                if (altTuple) {
                    var description = data["dc:description"];
                    if (description === undefined || description.trim() === "") {
                        description = data["dc:title"];
                    }
                    altTuple.seedTextValue(description);
                    altTuple.update();
                    toggleAlternativeFieldsAndLink(isDecorative);
                }
                if (captionTuple) {
                    var title = data["dc:title"];
                    captionTuple.seedTextValue(title);
                    captionTuple.update();
                }
                // show or hide "DynamicMedia section" depending on whether the file is DM
                var isFileDM = data["dam:scene7File"];
                if (isFileDM === undefined || isFileDM.trim() === "" || !areDMFeaturesEnabled) {
                    $dynamicMediaGroup.hide();
                } else {
                    $dynamicMediaGroup.show();
                    getSmartCropRenditions(data["dam:scene7File"]);
                }
            }
        });
    }

    /**
     * Helper function to get core image instance 'smartCropRendition' property
     * @param filePath
     */
    function retrieveInstanceInfo(filePath) {
        return $.ajax({
            url: filePath + ".json"
        }).done(function(data) {
            if (data) {
                // we need to get saved value of 'smartCropRendition' of Core Image component
                smartCropRenditionFromJcr = data["smartCropRendition"];
            }
        });
    }

    /**
     * Get the list of available image's smart crop renditions and fill drop-down list
     * @param imageUrl The link to image asset
     */
    function getSmartCropRenditions(imageUrl) {
        if (imagePropertiesRequest) {
            imagePropertiesRequest.abort();
        }
        imagePropertiesRequest = new XMLHttpRequest();
        var url = window.location.origin + "/is/image/" + imageUrl + "?req=set,json";
        imagePropertiesRequest.open("GET", url, true);
        imagePropertiesRequest.onload = function() {
            if (imagePropertiesRequest.status >= 200 && imagePropertiesRequest.status < 400) {
                // success status
                var responseText = imagePropertiesRequest.responseText;
                var rePayload = new RegExp(/^(?:\/\*jsonp\*\/)?\s*([^()]+)\(([\s\S]+),\s*"[0-9]*"\);?$/gmi);
                var rePayloadJSON = new RegExp(/^{[\s\S]*}$/gmi);
                var resPayload = rePayload.exec(responseText);
                var payload;
                if (resPayload) {
                    var payloadStr = resPayload[2];
                    if (rePayloadJSON.test(payloadStr)) {
                        payload = JSON.parse(payloadStr);
                    }

                }
                // check "relation" - only in case of smartcrop renditions
                if (payload !== undefined && payload.set.relation && payload.set.relation.length > 0) {
                    if (smartCropRenditionsDropDown.items) {
                        smartCropRenditionsDropDown.items.clear();
                    }
                    // we need to add "NONE" item first in the list
                    addSmartCropDropDownItem("NONE", "", true);
                    // "AUTO" would trigger automatic smart crop operation; also we need to check "AUTO" was chosed in previous session
                    addSmartCropDropDownItem("Auto", "SmartCrop:Auto", (smartCropRenditionFromJcr === "SmartCrop:Auto"));
                    for (var i = 0; i < payload.set.relation.length; i++) {
                        smartCropRenditionsDropDown.items.add({
                            content: {
                                innerHTML: payload.set.relation[i].userdata.SmartCropDef
                            },
                            disabled: false,
                            selected: (smartCropRenditionFromJcr === payload.set.relation[i].userdata.SmartCropDef)
                        });
                    }
                    $dynamicMediaGroup.find(presetTypeSelector).parent().show();
                } else {
                    $dynamicMediaGroup.find(presetTypeSelector).parent().hide();
                    selectPresetType($(presetTypeSelector), "imagePreset");
                }
                prepareSmartCropPanel();
            } else {
                // error status
            }
        };
        imagePropertiesRequest.send();
    }

    /**
     * Helper function for populating dropdown list
     */
    function addSmartCropDropDownItem(label, value, selected) {
        smartCropRenditionsDropDown.items.add({
            content: {
                innerHTML: label,
                value: value
            },
            disabled: false,
            selected: selected
        });
    }

    /**
     * Helper function to show/hide UI-elements of dialog depending on the chosen radio button
     */
    function prepareSmartCropPanel() {
        var presetType = getSelectedPresetType($(presetTypeSelector));
        switch (presetType) {
            case undefined:
                selectPresetType($(presetTypeSelector), "imagePreset");
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().hide();
                break;
            case "imagePreset":
                $dynamicMediaGroup.find(imagePresetDropDownSelector).parent().show();
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().hide();
                break;
            case "smartCrop":
                $dynamicMediaGroup.find(imagePresetDropDownSelector).parent().hide();
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().show();
                break;
            default:
                break;
        }
    }

    /**
     * Get selected radio option helper
     * @param component The radio option component
     * @returns {String} Value of the selected radio option
     */
    function getSelectedPresetType(component) {
        var radioComp = component.find('[type="radio"]');
        for (var i = 0; i < radioComp.length; i++) {
            if ($(radioComp[i]).prop("checked")) {
                return $(radioComp[i]).val();
            }
        }
        return undefined;
    }

    /**
     * Select radio option helper
     * @param component
     * @param val
     */
    function selectPresetType(component, val) {
        var radioComp = component.find('[type="radio"]');
        radioComp.each(function() {
            $(this).prop("checked", ($(this).val() === val));
        });
    }

    /**
     * Reset selection field
     * @param field
     */
    function resetSelectField(field) {
        if (field[0]) {
            field[0].clear();
        }
    }

})(jQuery);
