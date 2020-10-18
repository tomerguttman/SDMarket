var SELECTED_STORE_ORDERS_HISTORY_URL = buildUrlWithContextPath("getSelectedStoreOrdersHistory");
var REFRESH_TABLE_OWNER_URL = buildUrlWithContextPath("getTableOwnerInformation")
var CREATE_STORE_URL = buildUrlWithContextPath("createStore");
var currentOrdersHistory;
var currentZoneStores;
var currentZoneItems;

$(document).ready(function(){
    refreshTableOwnerInformation();
    //setInterval(refreshTableOwnerInformation, 1000);
})

function createStoreOption(store) {
    const storeName = store.name;
    return $('<option value="' + storeName + '">' + storeName + '</option>"');
}

function updateOrderHistoryStorePickerSelectBox(storesAvailable) {
    currentZoneStores = storesAvailable;

    for(var store of storesAvailable){
        if(!($('#ordersHistoryStoreSelectBox option[value=' + '"' + store.name + '"]').length > 0)) {
            $("#ordersHistoryStoreSelectBox").append(createStoreOption(store));
        }
    }
}

function createFeedbackTableRow(feedback, feedbackNumber) {
    return $("<tr id='feedback'" + feedbackNumber + ">\n" +
        "<td>" + feedback.customerName + "</td>\n" +
        "<td>" + feedback.dateOfFeedback + "</td>\n" +
        "<td>" +
        '<div class="row">' +
        '<div class="col col-auto"><i class="fa fa-star"></i></div>' +
        '<div class="col col-auto"><i class="fa fa-star"></i></div>' +
        '<div class="col col-auto"><i class="fa fa-star"></i></div>' +
        '<div class="col col-auto"><i class="fa fa-star"></i></div>' +
        '<div class="col col-auto"><i class="fa fa-star"></i></div>' +
        '</div>' +
        "<td>" + feedback.review + "</td>\n" +
        "</tr>\n");
}

function updateRelevantFeedbacksInTable(feedbacks) {
    if(feedback !== null && feedback !== undefined) {
        $("#feedbackTable tbody").empty();
        var feedbackNumber = 1;

        for(var feedback of feedbacks){
            $("#feedbackTable tbody").append(createFeedbackTableRow(feedback, feedbackNumber));
            var feedbackId = "#feedback" + feedbackNumber;
            var ratingStars = $(feedbackId + "i");
            feedbackNumber += 1;

            for(var i = 0; i < feedback.rating; i++){
                ratingStars[i].setAttribute("style", "color: #00F0B5");
            }
        }
    }
}

function refreshTableOwnerInformation() {
    $.ajax({
        url : REFRESH_TABLE_OWNER_URL,
        type: "GET",
        success: function(data) {
            console.log('storesAvailable: ' + data.storesAvailable);
            updateOrderHistoryStorePickerSelectBox(data.storesAvailable);
            updateRelevantFeedbacksInTable(data.feedbacks);
            currentZoneItems = data.zoneItems;
        },
        error: function (data) {
            alert(data.message);
        }
    });
}

function updateOrdersHistoryTableForStore(ordersHistory) {
    if(ordersHistory.length !== 0){
        $('#ordersHistoryTable tbody').empty();
        for (var order of ordersHistory) {
            $('#ordersHistoryTable tbody').append(createOrderTableRow(order));
        }
    }
}

function createOrderButton(orderId) {
    const onclickMethod = "activateOrderDetailsModal(" + orderId + ");";
    return '<a id="orderBtn' + orderId + ' ' +
        'class="btn btn-primary btn-lg btn-sm" role="button" data-toggle="modal"' +
        'onclick=' + onclickMethod + ">" +
        "Order Details" +
        "</a>";
}

function createItemTableRow(item) {
    return $("<tr>\n" +
        "<td>" + item.Id + "</td>\n" +
        "<td>" + item.name + "</td>\n" +
        "<td>" + item.purchaseCategory + "</td>\n" +
        "<td>" + item.totalItemsSold + "</td>\n" +
        "<td>" + item.pricePerUnit + "</td>" +
        "<td>" + item.pricePerUnit * item.totalItemsSold + "</td>" +
        "<td>" + item.wasPartOfDiscount + "</td>" +
        "</tr>\n");
}

function activateOrderDetailsModal(orderId) {
    $('#orderDetailsModalTable tbody').empty();
    for(var item of currentOrdersHistory[orderId]) {
        $('#orderDetailsModalTable tbody').append(createItemTableRow(item));
    }
    $('#orderDetailsModal').modal('show');
}

function createOrderTableRow(order) {
    return $("<tr>\n" +
        "<td>" + order.orderId + "</td>\n" +
        "<td>" + order.dateOrderWasMade + "</td>\n" +
        "<td>" + order.customerName + "</td>\n" +
        "<td>" + order.orderDestination + "</td>\n" +
        "<td>" + order.averageOrdersCostWithoutDelivery + "</td>" +
        "<td>" + order.costOfItemsInOrder + "</td>" +
        "<td>" + order.deliveryCost + "</td>" +
        "<td>" + createOrderButton(order.orderId) + "</td>"+
        "</tr>\n");
}

$("#ordersHistoryPickStoreButton").click(() => {
    if($("#ordersHistoryStoreSelectBox").val() !== ""){
        const selectedStore = $("#ordersHistoryStoreSelectBox").val();
        if(selectedStore !== "" && selectedStore != null) {
            $.ajax({
                url : SELECTED_STORE_ORDERS_HISTORY_URL,
                data : {"selectedStore" : selectedStore},
                type: "GET",
                success: function(data) {
                    console.log('storeOrdersHistory: ' + data.ordersHistory);
                    currentOrdersHistory = data.ordersHistory;
                    updateOrdersHistoryTableForStore(data.ordersHistory);
                },
                error: function (data) {
                    alert(data.message);

                }
            });
        }
        else { alert("Please select an option before picking a store")}

    }
    else{
        alert("Please choose a file first");
    }
});

function isStoreNameUnique(storeName) {
    var validFlag = true;
    for (var store of currentZoneStores) {
        if(store.name === storeName) {
            validFlag = false;
            break;
        }
    }

    return validFlag;
}

function storeLocationUnique(xCoordinate, yCoordinate) {
    var validFlag = true;
    for (var store of currentZoneStores) {
        if(store.storeLocation.x === xCoordinate && store.storeLocation.y === yCoordinate) {
            validFlag = false;
            break;
        }
    }

    return validFlag;
}

function isStoreItemsValid(storeItemsDetails, storeItemsList) {
    var validItemsFlag = true;
    var itemToAddCounter = 0;

    for (var itemRow of storeItemsDetails) {
        if($(itemRow).find("[type='checkbox']").prop('checked')) {
            if($(itemRow).find("[type='number']").val() !== "") {
                storeItemsList.push({
                    "id" : parseInt($(itemRow).children()[0].textContent),
                    "price" : parseInt($(itemRow).find("[type='number']").val())
                });
                itemToAddCounter += 1;
            }
            else { alert("One of the included items does not contain a price"); return false; }
        }
    }
    if (itemToAddCounter === 0) { alert("A store must sell at least one item"); return false;}

    return validItemsFlag;
}

function validateInput(storeName, ppk, xCoordinate, yCoordinate, storeItemsDetails, storeItemsList) {
    var validFlag = false;
    if(storeName !== "") {
        if(isStoreNameUnique(storeName)) {
            if(storeLocationUnique(xCoordinate, yCoordinate)) {
                if(isStoreItemsValid(storeItemsDetails, storeItemsList)) {
                    validFlag = true;
                }
            }
            else { alert("The location entered already contains another store"); }
        }
        else { alert("The store name " + "'" + storeName + "'" + " already exists in the zone, please choose another one"); }
    }
    else { alert("Please enter a store name"); }

    return validFlag;
}

function resetCreateStoreModal() {
    $("#createStoreModal [name='storeName']").val("");
    const ppk = $("#createStoreModal [name='ppk']").val("");
    const xCoordinate = $("#createStoreModal [name='x']").val("");
    const yCoordinate = $("#createStoreModal [name='y']").val("");
    const storeItemsDetails = $("#createStoreModal .itemRow");

    for (var itemRow of storeItemsDetails) {
        $(itemRow).find("[type='number']").val("")
        if($(itemRow).find("[type='checkbox']").prop('checked')) {
            $(itemRow).find("[role='button']").addClass('btn-danger off').removeClass('btb-success');
        }
    }
}

$("#createStoreButton").click(() => {
    var storeItemsList = [];
    const storeName = $("#createStoreModal [name='storeName']").val();
    const ppk = $("#createStoreModal [name='ppk']").val();
    const xCoordinate = $("#createStoreModal [name='x']").val();
    const yCoordinate = $("#createStoreModal [name='y']").val();
    const storeItemsDetails = $("#createStoreModal .itemRow");

    $('#createStoreModal').modal("hide");
    if (validateInput(storeName, ppk, xCoordinate, yCoordinate, storeItemsDetails, storeItemsList) ) {
        $.ajax({
            url : CREATE_STORE_URL,
            data : {
                "storeName" : storeName,
                "ppk" : ppk,
                "xCoordinate" : xCoordinate,
                "yCoordinate" : yCoordinate,
                "storeItems" : JSON.stringify(storeItemsList)
            },
            type: "POST",
            success: function(data) {
                alert(data.message);
            },
            error: function (data) {
                alert(data.message);
            }
        });
    }

    resetCreateStoreModal();
});

function createPriceInputElement() {
    return '<input class="form-control" type="number" required="" style="width: 104px;" min="1">';
}

function createToggleButtonElement() {
    return '<input type="checkbox" data-toggle="toggle" data-onstyle="success" data-offstyle="danger" data-on="Yes" data-off="No">\n';
}

function createRowForCreateStoreItemsTable(item) {
    return $("<tr class='itemRow'>\n" +
        "<td>" + item.Id + "</td>\n" +
        "<td>" + item.name + "</td>\n" +
        "<td>" + createPriceInputElement() + "</td>\n" +
        "<td>" + createToggleButtonElement() + "</td>\n" +
        "</tr>\n");
}

$("#openCreateStoreModalButton").click(() => {
    $('#createStoreModal tbody').empty();
    resetCreateStoreModal();

    for(var item of currentZoneItems) {
        $('#createStoreModal tbody').append(createRowForCreateStoreItemsTable(item));
    }
});




