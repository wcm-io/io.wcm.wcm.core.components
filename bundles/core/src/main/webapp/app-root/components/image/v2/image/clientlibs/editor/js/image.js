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
  var fileReference;

  $(document).on("dialog-loaded", function(e) {
    var $dialog        = e.dialog;
    var $dialogContent = $dialog.find(dialogContentSelector);
    var dialogContent  = $dialogContent.length > 0 ? $dialogContent[0] : undefined;
    if (dialogContent) {
      isDecorative      = dialogContent.querySelector('coral-checkbox[name="./isDecorative"]');
      altTuple          = new CheckboxTextfieldTuple(dialogContent, 'coral-checkbox[name="./altValueFromDAM"]', 'input[name="./alt"]');
      $altGroup         = $dialogContent.find(".cmp-image__editor-alt");
      $linkURLGroup     = $dialogContent.find(".cmp-image__editor-link");
      if ($linkURLGroup) {
        $linkURLField     = $linkURLGroup.find('foundation-autocomplete[name="./linkURL"]');
      }
      captionTuple      = new CheckboxTextfieldTuple(dialogContent, 'coral-checkbox[name="./titleValueFromDAM"]', 'input[name="./jcr:title"]');
      $cqFileUpload     = $dialog.find(".cq-FileUpload");
      $cqFileUploadEdit = $dialog.find(".cq-FileUpload-edit");
      if ($cqFileUpload) {
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
        }
        else {
          altTuple.hideCheckbox(true);
          captionTuple.hideCheckbox(true);
        }
      }
      toggleAlternativeFieldsAndLink(isDecorative);
    }
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

  function toggleAlternativeFieldsAndLink(checkbox) {
    if (checkbox) {
      if (checkbox.checked) {
        if ($linkURLGroup) {
          $linkURLGroup.hide();
        }
        $altGroup.hide();
      }
      else {
        $altGroup.show();
        if ($linkURLGroup) {
          $linkURLGroup.show();
        }
      }
      if ($linkURLField) {
        var foundationField = $linkURLField.adaptTo("foundation-field");
        if (foundationField) {
          foundationField.setDisabled(checkbox.checked);
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
      }
    });
  }

})(jQuery);
