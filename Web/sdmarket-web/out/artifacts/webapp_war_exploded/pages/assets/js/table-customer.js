const GET_AVAILABLE_STORES_STATIC_URL = buildUrlWithContextPath("getAvailableStoresInZone");
const GET_AVAILABLE_ITEMS_URL = buildUrlWithContextPath("getAllAvailableItemsInZone");
const orderDetailsModalHref = '#orderDetailsModal';
var currentAvailableStoresMap;
var currentAvailableItemsMap;
var currentCartBucketListOfItems;

$(document).ready(function(){
    initializePurchaseForm();
    setInterval(initializePurchaseForm, 2000);
})

function createStoreOption(store) {
    const storeName = store.name;
    return $('<option value="' + storeName + '">' + storeName + '</option>"');
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
    for(let item of currentOrdersHistory[orderId]) {
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

function disableAndDeleteSelectBoxOptions() {
    $('#storePickerSelectBox option').remove();
    $('#storePickerSelectBox').prop('disabled', true);
    $('#pickStoreButton').prop('disabled', true);
}

function clearAllTables() {
    $('#availableItemsTable tbody').empty();
    $('#availableDiscountsTable tbody').empty();
    $('#discountOffersTable tbody').empty();
    $('#shoppingCartTable tbody').empty();
}

function createItemRowForAvailableItemsTable(availableItem, currentPurchaseMethod) {
    let isAvailable = currentPurchaseMethod === 'static' ? availableItem.isAvailable : 'True';
    return $("<tr class='itemRow'>\n" +
        "<td>" + availableItem.Id + "</td>\n" +
        "<td>" + availableItem.name + "</td>\n" +
        "<td>" + availableItem.purchaseCategory + "</td>\n" +
        "<td>" + availableItem.pricePerUnit + "</td>\n" +
        "<td>" + isAvailable + "</td>\n" +
        "</tr>\n");
}

function createItemSelectOption(availableItem) {
    const itemId = availableItem.Id;
    return $('<option value="item'+ itemId +'">' + itemId + ' | ' + availableItem.name + '</option>"');
}

function addItemToItemNameSelectBox(availableItem) {
    // $('#itemNameSelectBox option').remove(); -> was already done in reset method.
    const flag = (($('#itemNameSelectBox option[value= "item' + availableItem.Id + '"]')).length > 0);
    if(!flag) {
        $('#itemNameSelectBox').append(createItemSelectOption(availableItem));
    }

}

function loadAvailItemsToTable(data, currentPurchaseMethod) {
    const availableItems = data.availableItems;
    $('#availableItemsTable tbody').empty();
    for (var availableItem in availableItems) {
        $('#availableItemsTable tbody').append(createItemRowForAvailableItemsTable(availableItems[availableItem], currentPurchaseMethod));

        if(availableItems[availableItem].isAvailable === true || currentPurchaseMethod === 'dynamic') {
            addItemToItemNameSelectBox(availableItems[availableItem]);
        }
    }
}

function toJsonMapOfItems(availableItems) {
    var jsonMapItems = {};

    for (const sItem in availableItems) {
        jsonMapItems[availableItems[sItem].Id] =  availableItems[sItem];
    }

    return jsonMapItems;
}

function ajaxLoadAvailableItems(pickedStore) {
    var currentPurchaseMethod = $('#purchaseMethodToggle').prop('checked') ? "dynamic" : "static";

    $.ajax({
        url : GET_AVAILABLE_ITEMS_URL,
        type: "GET",
        data: {
            "currentPurchaseMethod": currentPurchaseMethod,
            "pickedStore": pickedStore
        },
        success: function(data) {
            loadAvailItemsToTable(data, currentPurchaseMethod);
            currentAvailableItemsMap = toJsonMapOfItems(data.availableItems);
        },
        error: function (data) {
            alert(data.message);
        }
    });
}

function loadAvailableStoreToSelectBox(data) {
    //$('#storePickerSelectBox option').remove(); was already made in reset method.
    for (var store in data.availableStore) {
        const flag = (($('#storePickerSelectBox option[value=' + '"' + data.availableStore[store].name + '"]')).length > 0);
        if(!flag) {
            $('#storePickerSelectBox').append(createStoreOption(data.availableStore[store]));
        }
    }
}

function ajaxLoadStorePicker() {
    $.ajax({
        url : GET_AVAILABLE_STORES_STATIC_URL,
        type: "GET",
        success: function(data) {
            loadAvailableStoreToSelectBox(data);
            currentAvailableStoresMap = toJsonMapOfStores(data.availableStores);
        },
        error: function (data) {
            alert(data.message);
        }
    });
}

function loadStorePicker() {
    ajaxLoadStorePicker();
}

function resetOrderDetails(purchaseMethod) {
    $('#datePicker').val("");
    $('#destinationX').val("");
    $('#destinationY').val("");
    $('#showOrderSummaryButton').removeAttr('href').attr('hidden', true);
    $('#discountsDiv').attr('hidden', true);
    $('#itemNameSelectBox option').remove();
    $('#storePickerSelectBox option').remove();
    clearAllTables();
}

function enableAndDeleteSelectBoxOptions() {
    //$('#storePickerSelectBox option').remove();
    $('#storePickerSelectBox').prop('disabled', false);
    $('#pickStoreButton').prop('disabled', false);
}

function initializePurchaseForm() {
    const purchaseMethod = $('#purchaseMethodToggle').prop('checked') ? 'dynamic' : 'static';
    if(purchaseMethod === 'dynamic') {
        disableAndDeleteSelectBoxOptions();
        ajaxLoadAvailableItems('dynamic');
    }
    else {
        loadStorePicker();
        enableAndDeleteSelectBoxOptions();
    }
}

$("#purchaseMethodToggle").change(() => {
    var purchaseMethod = 'static';
    if($('#purchaseMethodToggle').prop('checked')) { purchaseMethod = "dynamic"; }
    resetOrderDetails(purchaseMethod);
    initializePurchaseForm();
});

$('#pickStoreButton').click(() => {
   const pickedStoreForStaticPurchase = $('#storePickerSelectBox').val();
   ajaxLoadAvailableItems(pickedStoreForStaticPurchase); // The items are being loaded to the table and the item select box in this method !
});

function createNewCartItemTableRow(itemToAdd, amountOfItem, wasPartOfDiscount) {
    return $("<tr>\n" +
        "<td>" + itemToAdd.Id + "</td>\n" +
        "<td>" + itemToAdd.name + "</td>\n" +
        "<td>" + itemToAdd.purchaseCategory + "</td>\n" +
        "<td>" + amountOfItem + "</td>\n" +
        "<td>" + itemToAdd.pricePerUnit + "</td>\n" +
        "<td>" + wasPartOfDiscount + "</td>\n" +
        "</tr>\n");
}

function addItemToCartTable(itemId, amountOfItem, wasPartOfDiscount) {
    $('#shoppingCartTable tbody').append(createNewCartItemTableRow(currentAvailableItemsMap[itemId], amountOfItem, wasPartOfDiscount));
}

function validateAmountOfItemAndAddToCart(itemId, amountOfItem) {
    const selectedItem = currentAvailableItemsMap[itemId];
    var amountOfItemParsedFloat = parseFloat(amountOfItem);
    var res = (typeof(amountOfItemParsedFloat) === 'number') && ((amountOfItemParsedFloat%1) === 0);

    if( isNaN(amountOfItemParsedFloat) ) {
        alert("The amount you entered is not a number");
        $('#amountToAddTextInput').val("");
    }
    else {
        if(selectedItem.purchaseCategory === 'Quantity') {

            if(res) {
                addItemToCartTable(itemId, amountOfItem, "No");
            }
            else {
                alert("The amount you entered is not an integer");
                $('#amountToAddTextInput').val("");
            }
        }
        else {
            addItemToCartTable(itemId, amountOfItem, "No");
        }
    }
}

$('#addToCartButton').click(() => {
   const amountOfItem = $('#amountToAddTextInput').val();
   const itemId = $('#itemNameSelectBox').val().substr(4); // take the 'item' out of the text.
   validateAmountOfItemAndAddToCart(itemId, amountOfItem)
});

function generateCartItemsBucketForDiscounts(purchaseMethod) {
    const allCartTableRows = $('#shoppingCartTable tbody tr');
    currentCartBucketListOfItems = {};

    for (const itemInCart of allCartTableRows) {
        const itemId = itemInCart.find('td')[0];
        const amountToAdd = itemInCart.find('td')[3];
        if (currentCartBucketListOfItems[itemId] !== undefined) {
            currentCartBucketListOfItems[itemId] = currentCartBucketListOfItems[itemId] + parseFloat(amountToAdd);
        }
        else { currentCartBucketListOfItems[itemId] = parseFloat(amountToAdd); }
    }
}

function getDiscountsPerItemMap(store) {
    //for (const discount of store.storeDiscounts) {
        //store.storeDiscounts[discount]
    //}
}

function getRelevantDiscountsFromSpecificStoreAndAddToTable() {
    const selectedStore = $('#storePickerSelectBox').val();
    const discountOfSelectedStore = getDiscountsPerItemMap(currentAvailableStoresMap[selectedStore])
    //loadDiscountsToTable(discountOfSelectedStore);
}

function getRelevantDiscountFromWholeZoneAndAddToTable() {

}

function loadRelevantDiscountsToTable(purchaseMethod) {
    if (purchaseMethod === 'dynamic') { getRelevantDiscountFromWholeZoneAndAddToTable(); }
    else { getRelevantDiscountsFromSpecificStoreAndAddToTable(); }
}

$('#goToCheckoutButton').click(() => {
    const allCartTableRows = $('#shoppingCartTable tbody tr');

    if(allCartTableRows.length > 0) {
        $('#showOrderSummaryButton').attr('href', orderDetailsModalHref).removeAttr('hidden');
        $('#discountsDiv').removeAttr('hidden');
        const purchaseMethod = $('#purchaseMethodToggle').prop('checked') ? "dynamic" : "static";
        generateCartItemsBucketForDiscounts(purchaseMethod)
        //currentCartBucketListOfItems is now updated and ready for use.
        loadRelevantDiscountsToTable(purchaseMethod)
    }
    else {
        alert("You can't proceed to checkout with an empty cart");
    }
});

function toJsonMapOfStores(availableStores) {
    var jsonMapOfStores = {};

    for (const store in availableStores) {
        jsonMapOfStores[availableStores[store].name] =  availableStores[store];
    }

    return jsonMapOfStores;
}




