const GET_AVAILABLE_STORES_STATIC_URL = buildUrlWithContextPath("getAvailableStoresInZone");
const GET_AVAILABLE_ITEMS_URL = buildUrlWithContextPath("getAllAvailableItemsInZone");
let itemsInOrderList;
let dynamicItemToStoreMap;
let staticOrder;
let dynamicOrder;
let currentDiscountName;
let currentAvailableStoresMap;
let currentAvailableItemsMap;
let currentCartBucketListOfItems;
let currentDiscountsOfSelectedStore;

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
    for (var store in data.availableStores) {
        const flag = (($('#storePickerSelectBox option[value=' + '"' + data.availableStores[store].name + '"]')).length > 0);
        if(!flag) {
            $('#storePickerSelectBox').append(createStoreOption(data.availableStores[store]));
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
    $('#showOrderSummaryButton').attr('hidden', true);
    $('#discountsDiv').attr('hidden', true);
    $('#itemNameSelectBox option').remove();
    $('#storePickerSelectBox option').remove();
    $('#amountToAddTextInput').val("");
    clearAllTables();
    setDisablePropertyForPurchaseOrderComponents(false);
    $('#displayStoreOrderSummaryButton').removeAttr('hidden');

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

function createNewCartItemTableRow(itemToAdd, amountOfItem, wasPartOfDiscount, forAdditional) {
    let pricePerUnit = wasPartOfDiscount === 'Yes' ? forAdditional : itemToAdd.pricePerUnit;
    return $("<tr>\n" +
        "<td>" + itemToAdd.Id + "</td>\n" +
        "<td>" + itemToAdd.name + "</td>\n" +
        "<td>" + itemToAdd.purchaseCategory + "</td>\n" +
        "<td>" + amountOfItem + "</td>\n" +
        "<td>" + pricePerUnit + "</td>\n" +
        "<td>" + wasPartOfDiscount + "</td>\n" +
        "</tr>\n");
}

function addItemToCartTable(itemId, amountOfItem, wasPartOfDiscount, forAdditional) {
    if(wasPartOfDiscount === 'Yes') { $('#shoppingCartTable tbody').append(createNewCartItemTableRow(currentAvailableItemsMap[itemId], amountOfItem, wasPartOfDiscount, forAdditional));}
    else { $('#shoppingCartTable tbody').append(createNewCartItemTableRow(currentAvailableItemsMap[itemId], amountOfItem, wasPartOfDiscount)); }
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
                $('#amountToAddTextInput').val("");
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

function generateCartItemsBucketForDiscounts(purchaseMethod) {
    const allCartTableRows = $('#shoppingCartTable tbody tr');
    currentCartBucketListOfItems = {};

    for (const itemInCart of allCartTableRows) {
        const itemId = $(itemInCart).find('td')[0].textContent;
        const amountToAdd = ($(itemInCart).find('td')[3]).textContent;
        if (currentCartBucketListOfItems[itemId] !== undefined) {
            currentCartBucketListOfItems[itemId] = currentCartBucketListOfItems[itemId] + parseFloat(amountToAdd);
        }
        else { currentCartBucketListOfItems[itemId] = parseFloat(amountToAdd); }
    }
}

function createOfferRow(currentOffer) {
    let itemName = currentAvailableItemsMap[currentOffer.offerItemId].name;
    return $("<tr>\n" +
        "<td>" + currentOffer.offerItemId + "</td>\n" +
        "<td>" + itemName + "</td>\n" +
        "<td>" + currentOffer.quantity + "</td>\n" +
        "<td>" + currentOffer.forAdditional + "</td>\n" +
        "</tr>\n");
}

function pushDiscountOffersToOffersTable(currentItemDiscount) {
    for (const currentOffer of currentItemDiscount.getThat.offerList) {
        $('#discountOffersTable tbody').append(createOfferRow(currentOffer));
    }
}

function createOfferOption(offer) {
    const itemName = currentAvailableItemsMap[offer.offerItemId].name;
    return $('<option value="' + offer.offerItemId + '">' + offer.offerItemId + ' | ' + itemName + '</option>"');
}

function loadOfferOptionsToSelectBox(currentItemDiscount) {
    //remove all previous option as already made.
    for (const offer of currentItemDiscount.getThat.offerList ) {
        $('#offerItemsSelectBox').append(createOfferOption(offer));
    }
}

function initializeOfferItemsSelectBox(currentItemDiscount) {
    $('#offerItemsSelectBox option').remove();

    if(currentItemDiscount.getThat.operator === 'ONE-OF') {
        $('#offerItemsSelectBox').prop('disabled', false);
        loadOfferOptionsToSelectBox(currentItemDiscount);
    }
    else { $('#offerItemsSelectBox').prop('disabled', true); }
}

function loadDiscountOfferToOfferTable(discountName, storeName, itemToBuyId) {
    const purchaseMethod = $('#purchaseMethodToggle').prop('checked') ? 'dynamic' : 'static';
    let currentItemDiscounts;

    if(purchaseMethod === 'static') { currentItemDiscounts = currentDiscountsOfSelectedStore[itemToBuyId]; }
    else { currentItemDiscounts = dynamicItemToStoreMap[itemToBuyId].storeToBuyFrom.storeDiscounts[itemToBuyId]; }

    for (const discount in currentItemDiscounts) {
        if(currentItemDiscounts[discount].name === discountName) {
            pushDiscountOffersToOffersTable(currentItemDiscounts[discount]);
            initializeOfferItemsSelectBox(currentItemDiscounts[discount]);
            break;
        }
    }
}

function createDisplayOfferButton(itemDiscountName) {

    return '<button class="btn btn-primary btn-sm" type="button" name="'+ itemDiscountName + '" >' +
        "Display Offer" +
        "</button>";
}

function createNewDiscountRow(discount, storeName) {
    let itemName = currentAvailableItemsMap[discount.buyThis.itemId].name.replace(/\s/g,'');
    return $("<tr id=" + discount.name + ">\n" +
        "<td>" + discount.buyThis.itemId + "</td>\n" +
        "<td>" + itemName + "</td>\n" +
        "<td>" + discount.name + "</td>\n" +
        "<td>" + storeName + "</td>\n" +
        "<td>" + discount.getThat.operator + "</td>\n" +
        "<td>" + createDisplayOfferButton(discount.name) + "</td>\n" +
        "</tr>\n");
}

function isDiscountAlreadyExistsInDiscountTable(discountName) {
    return $('#availableDiscountsTable tbody tr[id="' + discountName + '"]').length > 0;
}

function loadDiscountsToTable(discountsOfSelectedStore, storeName) {
    for (const itemId in discountsOfSelectedStore) {
        currentDiscountsOfSelectedStore = currentDiscountsOfSelectedStore.concat(discountsOfSelectedStore[itemId]);
    }

    for (const itemId in currentCartBucketListOfItems) {
        let currentItemDiscounts = discountsOfSelectedStore[itemId]; // returns a list of discounts for the current item.
        if(currentItemDiscounts !== undefined) {
            for(const itemDiscount of currentItemDiscounts) {
                if (itemDiscount.buyThis.quantity <= currentCartBucketListOfItems[itemId]) {
                    //discount are unique, therefore we don't need to check if it already exists in the table.
                    if(!isDiscountAlreadyExistsInDiscountTable(itemDiscount.name.replace(/\s/g,''))) {
                        let rowToAdd = createNewDiscountRow(itemDiscount, storeName);
                        let button = $(rowToAdd).find('button')[0];
                        $(button).click(() => {
                            currentDiscountName = $(button).attr('name');
                            loadDiscountOfferToOfferTable(itemDiscount.name, storeName, itemId);
                        });
                        $('#availableDiscountsTable tbody').append(rowToAdd);
                    }
                }
            }
        }
    }
}

function getRelevantDiscountsFromSpecificStoreAndAddToTable() {
    const selectedStore = $('#storePickerSelectBox').val();
    const discountOfSelectedStore = currentAvailableStoresMap[selectedStore].storeDiscounts;
    loadDiscountsToTable(discountOfSelectedStore, selectedStore);
}

function getCheapestStoreForItem(itemId) {
    let minPriceForItem = -1;
    let storeToBuyFrom;
    for (const store in currentAvailableStoresMap) {
        if(minPriceForItem === (-1)) {
            if(currentAvailableStoresMap[store].itemsBeingSold[itemId] !== undefined) {
                minPriceForItem = currentAvailableStoresMap[store].itemsBeingSold[itemId].pricePerUnit;
                storeToBuyFrom = currentAvailableStoresMap[store];
            }
        }
        else {
            if(currentAvailableStoresMap[store].itemsBeingSold[itemId] !== undefined && currentAvailableStoresMap[store].itemsBeingSold[itemId].pricePerUnit < minPriceForItem ) {
                minPriceForItem = currentAvailableStoresMap[store].itemsBeingSold[itemId].pricePerUnit;
                storeToBuyFrom = currentAvailableStoresMap[store];
            }
        }
    }

    return {
        "storeToBuyFrom" : storeToBuyFrom,
        "minPriceForItem" : minPriceForItem
    };
}

function getRelevantDiscountFromWholeZoneAndAddToTable() {
    //working with the bucket...
    let itemToStoreMap = {};
    for (const itemId in currentCartBucketListOfItems) {
        itemToStoreMap[itemId] = getCheapestStoreForItem(itemId);
    }

    for (const itemId in itemToStoreMap) {
        loadDiscountsToTable(itemToStoreMap[itemId].storeToBuyFrom.storeDiscounts, itemToStoreMap[itemId].storeToBuyFrom.name);
    }

    dynamicItemToStoreMap = itemToStoreMap;
}

function loadRelevantDiscountsToTable(purchaseMethod) {
    currentDiscountsOfSelectedStore = [];
    if (purchaseMethod === 'dynamic') { getRelevantDiscountFromWholeZoneAndAddToTable(); }
    else { getRelevantDiscountsFromSpecificStoreAndAddToTable(); }
}

function getRowOfSelectedOfferItem(selectedOfferItemId) {
    let offersRows = $('#discountOffersTable tbody tr');
    for (const offerRow of offersRows) {
        if($(offerRow).find("td")[0].textContent === selectedOfferItemId) {
            return offerRow;
        }
    }
}

function findRelevantDiscount(discountName) {
    for (const discount in currentDiscountsOfSelectedStore) {
        if(currentDiscountsOfSelectedStore[discount].name === discountName) { return currentDiscountsOfSelectedStore[discount]; }
    }
}

function addSelectedOfferItemToCart(selectedOfferItemId, discountName) {
    let itemToAddRow = getRowOfSelectedOfferItem(selectedOfferItemId);
    let forAdditional = $(itemToAddRow).find("td")[3].textContent;
    addItemToCartTable(selectedOfferItemId, $(itemToAddRow).find("td")[2].textContent, 'Yes', forAdditional)
}

function resetDiscountOffersTableAndReloadAvailableDiscounts() {
    const purchaseMethod = $('#purchaseMethodToggle').prop('checked') ? "dynamic" : "static";
    $('#discountOffersTable tbody').empty();
    $('#offerItemsSelectBox option').remove();
    $('#availableDiscountsTable tbody').empty();
    loadRelevantDiscountsToTable(purchaseMethod);
    if($('#availableDiscountsTable tbody tr').length === 0) {
        //table is empty which means there are not discounts left to apply, [AUTO] proceed to show order summary.
        $('#showOrderSummaryButton').click();
    }
}

function toJsonMapOfStores(availableStores) {
    var jsonMapOfStores = {};

    for (const store in availableStores) {
        jsonMapOfStores[availableStores[store].name] =  availableStores[store];
    }

    return jsonMapOfStores;
}

function validateCoordinatesAgainstStoresLocations(xCoordinate, yCoordinate) {
    let xCoordinateParsed = parseInt(xCoordinate);
    let yCoordinateParsed = parseInt(yCoordinate);

    for (const storeKey in currentAvailableStoresMap) {
        if(currentAvailableStoresMap[storeKey].storeLocation.x === xCoordinateParsed &&
            currentAvailableStoresMap[storeKey].storeLocation.y === yCoordinateParsed) {
            return false;
        }
    }

    return true;
}

function validateAllInformationWasGivenForOrder() {
    let datePickerValue = $('#datePicker').val();
    let xCoordinate = $('#destinationX').val();
    let yCoordinate = $('#destinationY').val();
    let coordinatesFlag =  xCoordinate !== "" &&  yCoordinate !== "" ;
    if ( datePickerValue !== "" && datePickerValue !== undefined ) {
        if(coordinatesFlag) {
           if(parseInt(xCoordinate) === parseFloat(xCoordinate) && parseInt(yCoordinate) === parseFloat(yCoordinate)) {
                if(validateCoordinatesAgainstStoresLocations(xCoordinate, yCoordinate)) {
                    return true;
                }
                else { alert("Destination coordinates of an order cannot be the same as a store's location"); return false;}
           }
           else { alert("Please enter integer numbers as (x,y) coordinates"); return false;}
        }
        else { alert("Please enter both coordinates (x,y)"); return false;}
    }
    else { alert("Please enter a date for the order"); return false;}
}

function setDisablePropertyForPurchaseOrderComponents(booleanValue) {
    $('#storePickerSelectBox').prop('disabled', booleanValue);
    $('#pickStoreButton').prop('disabled', booleanValue);
    $('#destinationX').prop('disabled', booleanValue);
    $('#destinationY').prop('disabled', booleanValue);
    $('#itemNameSelectBox').prop('disabled', booleanValue);
    $('#amountToAddTextInput').prop('disabled', booleanValue);
    $('#addToCartButton').prop('disabled', booleanValue);
    $('#goToCheckoutButton').prop('disabled', booleanValue);
}

function createItemsInOrderList(shoppingCartRows) {
    //i think ohad is wrong
    itemsInOrderList = [];
    for (const itemRow of shoppingCartRows) {
        const tds = $(itemRow).find('td');
        let itemObject = {
            "Id" : parseInt(tds[0].textContent),
            "name" : tds[1].textContent,
            "purchaseCategory" : tds[2].textContent,
            "amount" : parseFloat(tds[3].textContent),
            "pricePerUnit" : parseInt(tds[4].textContent),
            "wasPartOfDiscount" :tds[5].textContent
        };

        itemsInOrderList.push(itemObject);
    }
}

function calculateOrderCost(itemsInOrder) {
    let sum = 0;
    itemsInOrder.forEach(item => { sum += item.pricePerUnit * item.amount});
    return sum;
}

function calculateOrderCostForDynamicPurchase(itemsInOrder) {
    let sum = 0;
    for (const item of itemsInOrder) {
        if(item.wasPartOfDiscount === 'No') { sum += dynamicItemToStoreMap[item.Id].minPriceForItem * item.amount; }
        else { sum += item.pricePerUnit * item.amount; }
    }
    return sum;
}

function calculateDistance(xCoordinate, yCoordinate, storeLocation) {
    let firstArgument = Math.pow(storeLocation.x - xCoordinate, 2);
    let secondArgument = Math.pow(storeLocation.y - yCoordinate, 2);
    return Math.sqrt(firstArgument + secondArgument) ;
}

function createOrderSummaryItemRow(item) {
    return $("<tr>\n" +
        "<td>" + item.Id + "</td>\n" +
        "<td>" + item.name + "</td>\n" +
        "<td>" + item.purchaseCategory + "</td>\n" +
        "<td>" + item.amount + "</td>\n" +
        "<td>" + item.pricePerUnit + "</td>\n" +
        "<td>" + item.amount * item.pricePerUnit + "</td>\n" +
        "<td>" + item.wasPartOfDiscount + "</td>\n" +
        "</tr>\n");
}

function loadStoreItemListToTable(itemsInOrder) {
    $('#orderSummaryStoreItemsTable tbody').empty();
    for (const item of itemsInOrder) {
        $('#orderSummaryStoreItemsTable tbody').append(createOrderSummaryItemRow(item));
    }
}

function loadStaticOrderSummaryInformation(storeToOrderFrom, itemsInOrder, xCoordinate, yCoordinate) {
    let orderCost = calculateOrderCost(itemsInOrder);
    let distance = calculateDistance(xCoordinate, yCoordinate, storeToOrderFrom.storeLocation);
    let totalDeliveryCost = distance * storeToOrderFrom.deliveryPpk;
    $('#orderCostSpan').html("$" + orderCost.toFixed(2));
    $('#distanceSpan').html(distance.toFixed(2));
    $('#ppkSpan').html("$" + storeToOrderFrom.deliveryPpk);
    $('#totalDeliveryCostSpan').html("$" + totalDeliveryCost.toFixed(2));
    $('#deliveryCostSpan').html("$" + totalDeliveryCost.toFixed(2));
    $('#orderSummaryStoreNameSelectBox').append(createStoreOption(storeToOrderFrom));
    $('#displayStoreOrderSummaryButton').attr('hidden', true);
    loadStoreItemListToTable(itemsInOrder);
}

function generateStaticOrderAndFillOrderSummaryModal() {
    const storeToOrderFrom = currentAvailableStoresMap[$('#storePickerSelectBox').val()];
    const shoppingCartRows = $('#shoppingCartTable tbody tr');
    createItemsInOrderList(shoppingCartRows); //result is in global variable itemsInOrderList
    let xCoordinateParsed = parseInt($('#destinationX').val());
    let yCoordinateParsed = parseInt($('#destinationY').val());
    loadStaticOrderSummaryInformation(storeToOrderFrom, itemsInOrderList, xCoordinateParsed, yCoordinateParsed);
}

function getStoresParticipatingNameToStoreMap() {
    let storesParticipating = {};

    for (const itemId in dynamicItemToStoreMap) {
        storesParticipating[dynamicItemToStoreMap[itemId].storeToBuyFrom.name] = dynamicItemToStoreMap[itemId].storeToBuyFrom;
    }
    return storesParticipating;
}

function loadStoresToOrderSummaryStoresSelectBox(storesParticipating) {
    $('#orderSummaryStoreNameSelectBox option').remove();

    for (const storeKey in storesParticipating) {
        $('#orderSummaryStoreNameSelectBox').append(createStoreOption(storesParticipating[storeKey]));
    }
}

function calculateTotalDeliveryCostForDynamicPurchase(storesParticipating, xCoordinate, yCoordinate) {
    let totalDeliveryCost = 0;
    let currentStoreDistance;
    for (const storeKey in storesParticipating) {
        currentStoreDistance = calculateDistance(xCoordinate, yCoordinate, storesParticipating[storeKey].storeLocation);
        totalDeliveryCost += storesParticipating[storeKey].deliveryPpk * currentStoreDistance;
    }

    return totalDeliveryCost;
}

function loadDynamicOrderSummaryInformation(storesParticipating, itemsInOrder, xCoordinate, yCoordinate) {
    let totalOrderCost = calculateOrderCostForDynamicPurchase(itemsInOrder);
    let totalDeliveryCost = calculateTotalDeliveryCostForDynamicPurchase(storesParticipating, xCoordinate, yCoordinate);
    $('#orderCostSpan').html("$" + totalOrderCost.toFixed(2));
    $('#totalDeliveryCostSpan').html("$" + totalDeliveryCost.toFixed(2));
    loadStoresToOrderSummaryStoresSelectBox(storesParticipating)
}

function generateDynamicOrderAndFillOrderSummaryModal() {
    const storesParticipatingInOrder = getStoresParticipatingNameToStoreMap();
    const shoppingCartRows = $('#shoppingCartTable tbody tr');
    createItemsInOrderList(shoppingCartRows);
    let xCoordinateParsed = parseInt($('#destinationX').val());
    let yCoordinateParsed = parseInt($('#destinationY').val());
    loadDynamicOrderSummaryInformation(storesParticipatingInOrder, itemsInOrderList, xCoordinateParsed, yCoordinateParsed);
}

$("#purchaseMethodToggle").change(() => {
    var purchaseMethod = 'static';
    if($('#purchaseMethodToggle').prop('checked')) { purchaseMethod = "dynamic"; }
    resetOrderDetails(purchaseMethod);
    initializePurchaseForm();
});

$('#applyDiscountButton').click(() => {
    let discountName = currentDiscountName;
    if(currentDiscountName !== undefined) {
        if( $('#offerItemsSelectBox').prop('disabled')) {
            let discountOffersRows = $('#discountOffersTable tbody tr');
            for (const offerRow of discountOffersRows) {
                addSelectedOfferItemToCart($(offerRow).find('td')[0].textContent, discountName);
            }
        }
        else {
            //take the item from selectBox and add to cart.
            if($('#offerItemsSelectBox').val() !== "" && $('#offerItemsSelectBox').val() !== undefined) {
                let selectedOfferItemId = $('#offerItemsSelectBox').val();
                addSelectedOfferItemToCart(selectedOfferItemId, discountName);
            }
            else { alert('Please choose one of the available offers first'); }
        }

        //subtract once for every discount.
        let discount = findRelevantDiscount(discountName);
        let amountToSubtract = discount.buyThis.quantity;
        currentCartBucketListOfItems[discount.buyThis.itemId] = currentCartBucketListOfItems[discount.buyThis.itemId] - amountToSubtract;
        resetDiscountOffersTableAndReloadAvailableDiscounts();
    }
})

$('#goToCheckoutButton').click(() => {
    if(validateAllInformationWasGivenForOrder()) {
        const allCartTableRows = $('#shoppingCartTable tbody tr');
        if(allCartTableRows.length > 0) {
            $('#itemNameSelectBox option').remove();
            setDisablePropertyForPurchaseOrderComponents(true);
            $('#showOrderSummaryButton').removeAttr('hidden');
            $('#discountsDiv').removeAttr('hidden');
            const purchaseMethod = $('#purchaseMethodToggle').prop('checked') ? "dynamic" : "static";
            generateCartItemsBucketForDiscounts(purchaseMethod)
            //currentCartBucketListOfItems is now updated and ready for use.
            loadRelevantDiscountsToTable(purchaseMethod)
            if($('#availableDiscountsTable tbody tr').length === 0) {
                //table is empty which means there are not discounts left to apply, [AUTO] proceed to show order summary.
                $('#showOrderSummaryButton').click();
            }
        }
        else {
            alert("You can't proceed to checkout with an empty cart");
        }
    }

    return false;
});

$('#addToCartButton').click(() => {
    const amountOfItem = $('#amountToAddTextInput').val();
    const itemId = $('#itemNameSelectBox').val().substr(4); // take the 'item' out of the text.
    validateAmountOfItemAndAddToCart(itemId, amountOfItem)
});

$('#pickStoreButton').click(() => {
    const pickedStoreForStaticPurchase = $('#storePickerSelectBox').val();
    clearAllTables();
    ajaxLoadAvailableItems(pickedStoreForStaticPurchase); // The items are being loaded to the table and the item select box in this method !
});

$('#showOrderSummaryButton').click(() => {
    const purchaseMethod = $('#purchaseMethodToggle').prop('checked') ? 'dynamic' : 'static';
    if( purchaseMethod === 'static') { generateStaticOrderAndFillOrderSummaryModal(); }
    else {
        //dont forget to add <option value="Store Name" hidden>Store Name</option>
        generateDynamicOrderAndFillOrderSummaryModal();
    }
});

function createItemInOrderMap(selectedStoreName) {
    let itemListOfSelectedStore = [];
    for (const item of itemsInOrderList) {
        if(item.wasPartOfDiscount === 'No') {
            if(dynamicItemToStoreMap[item.Id].storeToBuyFrom.name === selectedStoreName) {
                item.pricePerUnit = dynamicItemToStoreMap[item.Id].minPriceForItem;
            }
        }
        else {
            if(dynamicItemToStoreMap[item.Id].storeToBuyFrom.name === selectedStoreName) {
                itemListOfSelectedStore.push(item);
            }
        }
    }

    return itemListOfSelectedStore;
}

$('#displayStoreOrderSummaryButton').click(() => {
    const selectedStoreName = $('#orderSummaryStoreNameSelectBox').val();
    if (selectedStoreName !== undefined) {
        let pickedStore = currentAvailableStoresMap[selectedStoreName];
        let xCoordinateParsed = parseInt($('#destinationX').val());
        let yCoordinateParsed = parseInt($('#destinationY').val());
        let distance = calculateDistance(xCoordinateParsed, yCoordinateParsed, pickedStore.storeLocation);
        $('#distanceSpan').html(distance.toFixed(2));
        $('#ppkSpan').html("$" + pickedStore.deliveryPpk);
        $('#deliveryCostSpan').html("$" + (pickedStore.deliveryPpk * distance).toFixed(2));
        loadStoreItemListToTable(createItemInOrderMap(selectedStoreName));
    }
    else { alert("Please select a store first"); }
});








