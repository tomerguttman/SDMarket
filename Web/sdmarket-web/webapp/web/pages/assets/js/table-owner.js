var SELECTED_STORE_ORDERS_HISTORY_URL = buildUrlWithContextPath("getSelectedStoreOrdersHistory");
var REFRESH_TABLE_OWNER_URL = buildUrlWithContextPath("getTableOwnerInformation")
var CREATE_STORE_URL = buildUrlWithContextPath("createStore");
var CREATE_NEW_ITEM_URL = buildUrlWithContextPath("createNewItem");
var currentOrdersHistory;
var currentZoneStores;
var currentUserZoneStores;
var currentZoneItems;
var currentNotifications = [];

$(document).ready(function(){
    refreshTableOwnerInformation();
    appendPurchaseCategoryOptions();
    setInterval(refreshTableOwnerInformation, 1000);
})

function appendPurchaseCategoryOptions() {
    $('#selectPurchaseCategory option').remove();
    $('#selectPurchaseCategory').append($('<option value = "undefined" hidden > Choose Purchase Category </option>'));
    $('#selectPurchaseCategory').append($('<option value = "Weight"> Weight </option>'));
    $('#selectPurchaseCategory').append($('<option value = "Quantity" > Quantity </option>'));
}

function createStoreOption(store) {
    const storeName = store.name;
    return $('<option value="' + storeName + '">' + storeName + '</option>"');
}

function updateOrderHistoryStorePickerSelectBox(storesAvailable) {
    if(storesAvailable !== undefined && storesAvailable !== null) {
        currentUserZoneStores = storesAvailable;
        for(var store of storesAvailable){
            if(!($('#ordersHistoryStoreSelectBox option[value=' + '"' + store.name + '"]').length > 0)) {
                $("#ordersHistoryStoreSelectBox").append(createStoreOption(store));
            }
        }
    }
}

function createFeedbackTableRow(feedback, feedbackNumber) {
    return $("<tr id='feedback" + feedbackNumber + "'>\n" +
        "<td>" + feedback.customerName + "</td>\n" +
        "<td>" + feedback.storeName + "</td>\n" +
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
    if(feedbacks !== null && feedbacks !== undefined) {
        $("#feedbackTable tbody").empty();
        var feedbackNumber = 1;

        for(var feedback of feedbacks){
            $("#feedbackTable tbody").prepend(createFeedbackTableRow(feedback, feedbackNumber));
            var feedbackId = "#feedback" + feedbackNumber;
            var ratingStars = $(feedbackId);
            feedbackNumber += 1;

            for(var i = 0; i < feedback.rating; i++){
                $(ratingStars).find('.fa')[i].setAttribute("style", "color: #00F0B5");
            }
        }
    }
}

function updateWholeZoneCurrentAvailableStores(wholeZoneStores) {
    currentZoneStores = wholeZoneStores;
}

function updateAddNewItemButtonHiddenProperty(isZoneOwner) {
    if(isZoneOwner) { $('#addNewItemButton').removeAttr("hidden"); }
}

function refreshTableOwnerInformation() {
    $.ajax({
        url : REFRESH_TABLE_OWNER_URL,
        type: "GET",
        data: { "amountOfNotifications" : currentNotifications.length },
        success: function(data) {
            // console.log('storesAvailable: ' + data.storesAvailable);
            updateWholeZoneCurrentAvailableStores(data.wholeZoneStores);
            updateOrderHistoryStorePickerSelectBox(data.storesAvailable);
            updateRelevantFeedbacksInTable(data.feedbacks);
            updateDashboardNotificationsDropdownMenu(data.notifications);
            $('#usernameTopRightSpan').html(data.userName);
            updateAddNewItemButtonHiddenProperty(data.isZoneOwner);
            currentZoneItems = data.zoneItems;
        },
        error: function (data) {
            alert(data.message);
        }
    });
}

function updateOrdersHistoryTableForStore(ordersHistory) {
    $('#ordersHistoryTable tbody').empty();

    if(ordersHistory.length !== 0){
        for (var order of ordersHistory) {
            $('#ordersHistoryTable tbody').prepend(createOrderTableRow(order));
        }
    }
}

function createOrderButton(orderId) {
    const onclickMethod = "activateOrderDetailsModal(" + orderId + ");";
    return '<a id="orderBtn' + orderId + '"' +
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

    for(var index in currentOrdersHistory) {
        if(currentOrdersHistory[index].orderId === orderId){
            const itemsInOrder = currentOrdersHistory[index].itemsInOrder;
            for(let item in itemsInOrder) {
                $('#orderDetailsModalTable tbody').append(createItemTableRow(itemsInOrder[item]));
            }

            break;
        }
    }

    $('#orderDetailsModal').modal('show');
}

function createOrderTableRow(order) {
    return $("<tr>\n" +
        "<td>" + order.orderId + "</td>\n" +
        "<td>" + order.dateOrderWasMade + "</td>\n" +
        "<td>" + order.customerName + "</td>\n" +
        "<td>(" + order.orderDestination.x + "," + order.orderDestination.y + ")</td>\n" +
        "<td>" + order.amountItemsInOrder + "</td>" +
        "<td>" + order.costOfItemsInOrder.toFixed(2) + "</td>" +
        "<td>" + order.deliveryCost.toFixed(2) + "</td>" +
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
    let xCoordinateParsed = parseInt(xCoordinate);
    let yCoordinateParsed = parseInt(yCoordinate);
    var validFlag = true;
    for (var store of currentZoneStores) {
        if(store.storeLocation.x === xCoordinateParsed && store.storeLocation.y === yCoordinateParsed) {
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

function isStoresValid(storeDetailsRows, storesToAddItemToList) {
    var validStoresFlag = true;
    var storeThatSellsCounter = 0;

    for (var storeRow of storeDetailsRows) {
        if($(storeRow).find("[type='checkbox']").prop('checked')) {
            if($(storeRow).find("[type='number']").val() !== "") {
                if(parseInt($(storeRow).find("[type='number']").val()) === parseFloat($(storeRow).find("[type='number']").val())){
                    storesToAddItemToList.push({
                        "storeId" : parseInt($(storeRow).children()[0].textContent),
                        "price" : parseInt($(storeRow).find("[type='number']").val())
                    });
                    storeThatSellsCounter += 1;
                }
                else{ alert("An item's price must be an integer"); return false;}
            }
            else { alert("One of the included stores does not have a price for the new item"); return false; }
        }
    }
    if (storeThatSellsCounter === 0) { alert("At least one store must sell the new item"); return false;}

    return validStoresFlag;
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

function validateNewItemInput(itemName, purchaseCategory,  storeDetailsRows, storesToAddItemToList) {
    var validFlag = false;
    if(itemName !== "") {
        if(purchaseCategory !== "undefined") {
            if(isStoresValid(storeDetailsRows, storesToAddItemToList)) {
                validFlag = true;
            }
        }
        else{ alert("Please choose a purchase category"); }
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
            success: function (data) {
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

function createRowForCreateNewItemTable(store) {
    return $("<tr class='storeRow'>\n" +
        "<td>" + store.Id + "</td>\n" +
        "<td>" + store.name + "</td>\n" +
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

function createDropdownMenuNotification(notification) {
    let bgClass;
    let iconClass;

    if (notification.notificationType === "storeCreatedNotification" ){
        bgClass = 'bg-primary';
        iconClass = 'fas fa-store text-white';
    }
    else if (notification.notificationType === "feedbackNotification" ) {
        bgClass = 'bg-danger';
        iconClass = 'fas fa-star text-white';
    }
    else {
        bgClass = 'bg-success';
        iconClass = 'fas fa-shopping-cart text-white';
    }

    return $('<a class="d-flex align-items-center dropdown-item" href="#">\n' +
        '<div class="mr-3">\n' +
            '<div class="' + bgClass+ ' icon-circle">\n' +
                '<i class="' + iconClass + '"></i>\n' +
            '</div>\n' +
        '</div>\n' +
        '<div>\n' +
        '<span class="small text-gray-500">' + notification.dateOfNotification + '</span>\n' +
        '<p>' + notification.subject + '</p>\n' +
        '</div>\n' +
        '</a>\n');
}

function updateDashboardNotificationsDropdownMenu(notifications) {
    if(notifications !== undefined && notifications !== null) {
        notifications.forEach(notification => currentNotifications.push(notification));
        if (currentNotifications.length !== 0) {
            if ($('#notificationsCounterSpan').text() === "0") {
                $('#dropDownNotificationsMenu a').remove();
            }

            for (const notification of notifications) {
                $('#dropDownNotificationsMenu').prepend(createDropdownMenuNotification(notification));
            }
        } else {
            $('#dropDownNotificationsMenu a').remove();
            let noNotifications = $('<a class="text-cetner dropdown-item small text-gray-500">No notifications to show</a>')
            $('#dropDownNotificationsMenu').append(noNotifications);
        }

        $('#notificationsCounterSpan').text(currentNotifications.length);
    }
}

function resetAddNewItemModal() {
    $("#addNewItemModal [name='itemName']").val("");
    $("#addNewItemModal [name='purchaseCategory']").val("undefined");
    $("#addNewItemModal tbody").empty();
}

$('#addNewItemButton').click( () => {
    $('#addNewItemModal tbody').empty();
    resetAddNewItemModal();

    for(var store of currentZoneStores) {
        if(store.ownerName === $('#usernameTopRightSpan').html()) {
            $('#addNewItemModal tbody').append(createRowForCreateNewItemTable(store));
        }
    }
});

$("#createNewItemButton").click(() => {
    var storesToAddItemToList = [];
    const itemName = $("#addNewItemModal [name='itemName']").val();
    const purchaseCategory = $("#addNewItemModal [name='purchaseCategory']").val();
    const storeDetailsRows = $("#addNewItemModal tbody tr");

    if (validateNewItemInput(itemName, purchaseCategory, storeDetailsRows, storesToAddItemToList) ) {
        $.ajax({
            url : CREATE_NEW_ITEM_URL,
            data : {
                "itemName" : itemName,
                "purchaseCategory" : purchaseCategory,
                "storesToAddItemToList" : JSON.stringify(storesToAddItemToList)
            },
            type: "POST",
            success: function (data) {
                $('#addNewItemModal').modal("hide");
                alert(data.message);
            },
            error: function (data) {
                $('#addNewItemModal').modal("hide");
                alert(data.message);
            }
        });
    }

    resetAddNewItemModal();
});
