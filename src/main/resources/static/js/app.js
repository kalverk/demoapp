/**
 * DSS Hwcrypto Demo
 *
 * Copyright (c) 2015 Estonian Information System Authority
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
$(document).on('change', '.btn-file :file', function() {
    var input = $(this),
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
    disableSign();
    hideDownloadSection();
    $("#fileName").val(label);
    $('#fileUpload').submit();
});

$(document).on("click", "#signButton", function(event) {
    event.preventDefault();
    sign();
});

$(document).on("click", "#authButton", function(event) {
    event.preventDefault();
    auth();
});

$(document).on("click", "#mobileAuthButton", function(event) {
    event.preventDefault();
    authMobile();
});

$(document).on("click", "#mobileSignButton", function(event) {
    event.preventDefault();
    signMobile();
});

$(document).ready(function() {
    $('#fileUpload').submit(function(e) {
        if($('#file').val()) {
            e.preventDefault();
            var progressBar = $("#progress-bar");
            $(this).ajaxSubmit({
                beforeSubmit: function() {
                    progressBar.width('0%');
                    progressBar.removeClass("progress-bar-success");
                },
                uploadProgress: function (event, position, total, percentComplete){
                    console.log("percent complete: "+ percentComplete);
                    progressBar.width(percentComplete + '%');
                    progressBar.html('<span>' + percentComplete +' %</span>');
                },
                success: function (){
                    console.log("successfully uploaded file");
                    progressBar.addClass("progress-bar-success");
                    enableSign();
                },
                error: function(xhr, textStatus, error){
                    console.log("error uploading file: " + error)
                },
                resetForm: false
            });
            return false;
        }
    });
});

enableSign = function() {
    $("#signButton").addClass("btn-success").prop('disabled', false);
    $("#mobileSignButton").addClass("btn-success").prop('disabled', false);
};

disableSign = function() {
    $("#signButton").removeClass("btn-success").prop('disabled', true);
    $("#mobileSignButton").removeClass("btn-success").prop('disabled', true);
};

showDownloadSection = function() {
    var downloadSection = $("#downloadSection");
    if(downloadSection.hasClass("hidden")) {
        downloadSection.toggleClass("show hidden");
    }
};

hideDownloadSection = function() {
    var downloadSection = $("#downloadSection");
    if(downloadSection.hasClass("show")) {
        downloadSection.toggleClass("show hidden");
    }
};

post = function(url, data) {
    $("#output").text("");
    $('#mobileno').parent().removeClass('has-warning');
    $('#idcode').parent().removeClass('has-warning');
    return new Promise(function(resolve, reject) {
        $.ajax({
            dataType: "json",
            url: url,
            type: "POST",
            data: data
        }).done(function(data) {
            if(data.result != "ok") {
                reject(Error(data.result))
            } else {
                resolve(data);
            }
        }).fail(function() {
            reject(Error("Post operation failed"));
        });
    });
};

fetchHash = function(certInHex) {
    return post("generateHash", {cert:certInHex})
};

createContainer = function(signatureInHex) {
    return post("createContainer", {signatureInHex:signatureInHex});
};

authUser = function(certificate) {
    return post("identify", {certificate:certificate});
};

authWithMobile = function(id,phoneNr) {
    return post("mobileauth", {id:id,phoneNumber:phoneNr});
};

signWithMobile = function(id, phoneNr) {
    return post("mobilesign",{id:id,phoneNumber:phoneNr});
};

//Used for user auth .getCertificate asks for pin1
/*NOTE THAT : the certification selection is bound the the lifecycle of the window object: re-loading the page invalidates the selection and calling sign() is not possible.*/
auth = function() {
    window.hwcrypto.getCertificate({lang: 'en'}).then(function(certificate) {
        var b64encoded = btoa(String.fromCharCode.apply(null, certificate.encoded));
        return userData = authUser(b64encoded);
    }).then(function(digest){
        //TODO display data to user
        $("#output").text("I know who you are: " + digest.hex);
    });
};

sign = function() {
    var cert;
    window.hwcrypto.getCertificate({lang: 'en'}).then(function(certificate) {
        cert = certificate;
        var b64encoded = btoa(String.fromCharCode.apply(null, certificate.encoded));
        return fetchHash(b64encoded);
    }).then(function(digest) {
        console.log("digest " + digest);
        return window.hwcrypto.sign(cert, {type: 'SHA-256', hex: digest.hex}, {lang: 'en'});
    }).then(function(signature) {
        console.log("signature " + signature);
        return createContainer(signature.hex);
    }).then(function(result) {
        showDownloadSection();
        console.log("container is ready for download");
    });
};

//Used for mobile authentication
authMobile = function() {
    var ok = true;
    if( $('#idcode').val().length === 0 ) {
        $('#idcode').addClass('warning');
        ok = false;
    }
    if( $('#mobileno').val().length === 0 ) {
        $('#mobileno').addClass('warning');
        ok = false;
    }
    if(ok){
        authWithMobile($('#idcode').val(),$('#mobileno').val()).then(function(digest){
            //TODO display res to user
            console.log("Display data to user");
            $("#output").text("I know who you are: " + digest.hex);
        });
    }
};

signMobile = function() {
    //TODO control this only when user may not have previously authenticated
    var ok = true;
    if( $('#idcode').val().length === 0 ) {
        $('#idcode').parent().addClass('has-warning');
        ok = false;
    }
    if( $('#mobileno').val().length === 0 ) {
        $('#mobileno').parent().addClass('has-warning');
        ok = false;
    }
    if(ok){
        var res = signWithMobile($('#idcode').val(),$('#mobileno').val());
        //TODO display res to user
        console.log("Display data to user");
        console.log(res);
    }
};