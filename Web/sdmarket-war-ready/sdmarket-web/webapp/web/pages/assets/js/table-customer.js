const GET_AVAILABLE_STORES_STATIC_URL = buildUrlWithContextPath("pages/getAvailableStoresInZone");
const GET_AVAILABLE_ITEMS_URL = buildUrlWithContextPath("pages/getAllAvailableItemsInZone");
const POST_ORDER_URL = buildUrlWithContextPath("pages/postOrder");
const POST_FEEDBACKS_URL = buildUrlWithContextPath("pages/postFeedbacks");
const GET_CUSTOMER_ORDER_HISTORY_IN_ZONE_URL = buildUrlWithContextPath("pages/getOrderHistoryInZone");
let currentFeedbackMap;
let itemsInOrderList;
let dynamicItemToStoreMap;
let staticOrder;
let dynamicOrder;
let currentDiscountName;
let currentAvailableStoresMap;
let currentAvailableItemsMap;
let currentCartBucketListOfItems;
let currentDiscountsOfSelectedStore;
let currentOrdersHistory = {};

$(document).ready(function(){
    initializePurchaseForm();
    setInterval(initializePurchaseForm, 2000);
})

function createItemTableRowForOrderHistory(item, order) {
    let storeInfo = (order.storeName === "Dynamic Order " + order.orderId) ? ("Dynamic Order " + order.orderId) : (order.storeId + " | " + order.storeName);
    return $("<tr>\n" +
        "<td>" + item.Id + "</td>\n" +
        "<td>" + item.name + "</td>\n" +
        "<td>" + item.purchaseCategory + "</td>\n" +
        "<td>" + storeInfo + "</td>\n" +
        "<td>" + item.totalItemsSold + "</td>" +
        "<td>" + item.pricePerUnit + "</td>" +
        "<td>" + (item.pricePerUnit * item.totalItemsSold).toFixed(2) + "</td>" +
        "<td>" + item.wasPartOfDiscount + "</td>" +
        "</tr>\n");
}

function activateOrderDetailsModal(orderId) {
    $('#orderDetailsModal tbody').empty();
    for(let item of currentOrdersHistory[orderId].itemsInOrder) {
        $('#orderDetailsModal tbody').append(createItemTableRowForOrderHistory(item, currentOrdersHistory[orderId]));
    }
    $('#orderDetailsModal').modal('show');
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

function resetOrderDetails() {
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

function createOrderDetailsButton(orderId) {
    let onclickMethod = "activateOrderDetailsModal(" + orderId + ");";
    return '<a id="orderDetailsBtn' + orderId + '"' +
        'class="btn btn-primary btn-lg btn-sm" role="button" data-toggle="modal"' +
        'onclick=' + onclickMethod + ">" +
        "Order Details" +
        "</a>";
}

function createOrderHistoryTableRow(order) {
    return $("<tr>\n" +
        "<td>" + order.orderId + "</td>\n" +
        "<td>" + order.dateOrderWasMade + "</td>\n" +
        "<td>(" + order.orderDestination.x + "," + order.orderDestination.y + ")</td>\n" +
        "<td>" + order.amountOfStoresRelatedToOrder + "</td>\n" +
        "<td>" + order.amountItemsInOrder + "</td>" +
        "<td>" + order.costOfItemsInOrder.toFixed(2) + "</td>" +
        "<td>" + order.deliveryCost.toFixed(2) + "</td>" +
        "<td>" + order.totalOrderCost .toFixed(2)+ "</td>" +
        "<td>" + createOrderDetailsButton(order.orderId) + "</td>"+
        "</tr>\n");
}

function loadOrderHistoryTable(orderHistory) {

    $('#orderHistoryTable tbody').empty();

    for(const orderId in orderHistory){
        $('#orderHistoryTable tbody').prepend(createOrderHistoryTableRow(orderHistory[orderId]));
    }
}

function createStoreOption(store) {
    const storeName = store.name;
    return $('<option value="' + storeName + '">' + storeName + '</option>"');
}

function ajaxOrderHistory() {
    $.ajax({
        url : GET_CUSTOMER_ORDER_HISTORY_IN_ZONE_URL,
        type: "GET",
        success: function(data) {
            loadOrderHistoryTable(data.ordersHistory);
            currentOrdersHistory = data.ordersHistory;
            $('#usernameTopRightSpan').html(data.userName);
        },
        error: function (data) {
            alert(data.message);
        }
    });
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

    ajaxOrderHistory();
}

function createNewCartItemTableRow(itemToAdd, amountOfItem, wasPartOfDiscount, forAdditional, discountName) {
    let pricePerUnit = wasPartOfDiscount === 'Yes' ? forAdditional : itemToAdd.pricePerUnit;
    let discountNameToAdd = (discountName !== undefined) ? discountName.replace(/\s/g,'_') : "";
    return $("<tr id='" + discountNameToAdd + "'>\n" +
        "<td>" + itemToAdd.Id + "</td>\n" +
        "<td>" + itemToAdd.name + "</td>\n" +
        "<td>" + itemToAdd.purchaseCategory + "</td>\n" +
        "<td>" + amountOfItem + "</td>\n" +
        "<td>" + pricePerUnit + "</td>\n" +
        "<td>" + wasPartOfDiscount + "</td>\n" +
        "</tr>\n");
}

function addItemToCartTable(itemId, amountOfItem, wasPartOfDiscount, forAdditional, discountName) {
    if(wasPartOfDiscount === 'Yes') { $('#shoppingCartTable tbody').append(createNewCartItemTableRow(currentAvailableItemsMap[itemId], amountOfItem, wasPartOfDiscount, forAdditional, discountName));}
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
    $('#discountOffersTable tbody').empty();

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

    if(purchaseMethod === 'static') { currentItemDiscounts = currentAvailableStoresMap[storeName].storeDiscounts[itemToBuyId]; }
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
    return $("<tr id=" + discount.name.replace(/\s/g,'') + ">\n" +
        "<td>" + discount.buyThis.itemId + "</td>\n" +
        "<td>" + itemName + "</td>\n" +
        "<td>" + discount.name + "</td>\n" +
        "<td>" + storeName + "</td>\n" +
        "<td>" + discount.getThat.operator + "</td>\n" +
        "<td>" + createDisplayOfferButton(discount.name) + "</td>\n" +
        "</tr>\n");
}

function isDiscountAlreadyExistsInDiscountTable(discountName) {
    return $('#availableDiscountsTable tbody tr[id="' + discountName.replace(/\s/g,'') + '"]').length > 0;
}

function loadDiscountsToTable(discountsOfSelectedStore, storeName, itemId) {
    currentDiscountsOfSelectedStore = discountsOfSelectedStore[itemId];

    if(currentDiscountsOfSelectedStore !== undefined) {
        for(const itemDiscount of currentDiscountsOfSelectedStore) {
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

function loadDiscountsToTableForStaticPurchase(discountsOfSelectedStore, selectedStore) {
    let storeName = selectedStore;
    let currentItemDiscountsList;
    if(discountsOfSelectedStore !== undefined) {
        for(const itemId in discountsOfSelectedStore){
            currentItemDiscountsList = discountsOfSelectedStore[itemId];
            for(const discount of currentItemDiscountsList) {
                if(discount.buyThis.quantity <= currentCartBucketListOfItems[itemId]) {
                    let rowToAdd = createNewDiscountRow(discount, storeName);
                    let button = $(rowToAdd).find('button')[0];
                    $(button).click(() => {
                        currentDiscountName = $(button).attr('name');
                        loadDiscountOfferToOfferTable(discount.name, storeName, discount.buyThis.itemId);
                    });
                    $('#availableDiscountsTable tbody').append(rowToAdd);
                }
            }
        }
    }
}

function getRelevantDiscountsFromSpecificStoreAndAddToTable() {
    const selectedStore = $('#storePickerSelectBox').val();
    const discountsOfSelectedStore = currentAvailableStoresMap[selectedStore].storeDiscounts;
    loadDiscountsToTableForStaticPurchase(discountsOfSelectedStore, selectedStore);
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
        loadDiscountsToTable(itemToStoreMap[itemId].storeToBuyFrom.storeDiscounts, itemToStoreMap[itemId].storeToBuyFrom.name, itemId);
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
    for(let storeKey in currentAvailableStoresMap) {
        for(const itemKey in currentAvailableStoresMap[storeKey].storeDiscounts ){
            let discountArray = currentAvailableStoresMap[storeKey].storeDiscounts[itemKey];
            for(const index in discountArray) {
                if(discountArray[index].name === discountName) {
                    return discountArray[index];
                }
            }
        }
    }

    return null;
}

function addSelectedOfferItemToCart(selectedOfferItemId, discountName) {
    let itemToAddRow = getRowOfSelectedOfferItem(selectedOfferItemId);
    let forAdditional = $(itemToAddRow).find("td")[3].textContent;
    addItemToCartTable(selectedOfferItemId, $(itemToAddRow).find("td")[2].textContent, 'Yes', forAdditional, discountName)
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

function getStoreNameOfDiscount(itemId, discountName) {
    for (const store in currentAvailableStoresMap) {
        if ( isDiscountExistsInSelectedStore(discountName, currentAvailableStoresMap[store].name )) {
            return currentAvailableStoresMap[store].name;
        }
    }

    return null;
}

function createItemsInOrderList(shoppingCartRows) {
    itemsInOrderList = [];
    for (const itemRow of shoppingCartRows) {
        const tds = $(itemRow).find('td');
        let storeName = tds[5].textContent === 'Yes' ? getStoreNameOfDiscount(parseInt(tds[0].textContent), $(itemRow).attr("id")): "";
        let itemObject = {
            "Id" : parseInt(tds[0].textContent),
            "name" : tds[1].textContent,
            "purchaseCategory" : tds[2].textContent,
            "amount" : parseFloat(tds[3].textContent),
            "pricePerUnit" : parseInt(tds[4].textContent),
            "wasPartOfDiscount" :tds[5].textContent,
            "storeThatItemWasBoughtIn" : getCheapestStoreForItem(parseInt(tds[0].textContent)).storeToBuyFrom,
            "discountName" : $(itemRow).attr("id"),
            "storeNameOfDiscount" : storeName
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
    $('#orderSummaryStoreNameSelectBox option').remove();
    $('#orderSummaryStoreNameSelectBox').append(createStoreOption(storeToOrderFrom));
    $('#displayStoreOrderSummaryButton').attr('hidden', true);
    loadStoreItemListToTable(itemsInOrder);
    createStaticOrder(storeToOrderFrom, totalDeliveryCost, orderCost, distance, xCoordinate, yCoordinate, itemsInOrder);
}

function createStaticOrder(storeToOrderFrom, totalDeliveryCost, orderCost, distance, xCoordinate, yCoordinate, itemsInOrder) {
    staticOrder = {
        "purchaseMethod" : 'static',
        "storeId" : storeToOrderFrom.Id,
        "deliveryCost": totalDeliveryCost,
        "costOfItemsInOrder" : orderCost,
        "totalOrderCost": totalDeliveryCost + orderCost,
        "dateOrderWasMade" : $('#datePicker').val(),
        "storeName" : storeToOrderFrom.name,
        "orderDestination" : {
            "xCoordinate" : xCoordinate,
            "yCoordinate" : yCoordinate
        },
        "amountOfStoresRelatedToOrder" : 1,
        "itemsInOrder" : itemsInOrder
    };
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
    $('#ppkSpan').html("$0");
    $('#distanceSpan').html("0");
    $('#deliveryCostSpan').html("$0");
    $('#orderSummaryStoreItemsTable tbody tr').remove();
    loadStoresToOrderSummaryStoresSelectBox(storesParticipating)
}

function createDynamicOrder(storesParticipatingInOrder, totalDeliveryCost, orderCost, xCoordinate, yCoordinate, itemsInOrder, dateOfOrder) {
    dynamicOrder = {};
    dynamicOrder['storesParticipatingWithRelevantItems'] = {};
    for (const storeName in storesParticipatingInOrder) {
        dynamicOrder['storesParticipatingWithRelevantItems'][storeName] = [];
    }
    let storesMap = dynamicOrder['storesParticipatingWithRelevantItems'];

    for (const item of itemsInOrder) {
       if(item.wasPartOfDiscount === 'Yes') {
           storesMap[item.storeNameOfDiscount].push(item);
       }
       //this is a new change that might not work.
       else {
           item.pricePerUnit = dynamicItemToStoreMap[item.Id].minPriceForItem;
           storesMap[item.storeThatItemWasBoughtIn.name].push(item);
       }

    }

    dynamicOrder['totalDeliveryCost'] = totalDeliveryCost;
    dynamicOrder['orderCost'] = orderCost;
    dynamicOrder['location'] = {
        "xCoordinate" : xCoordinate,
        "yCoordinate" : yCoordinate
    };
    dynamicOrder['date'] = dateOfOrder;
}

function generateDynamicOrderAndFillOrderSummaryModal() {
    const storesParticipatingInOrder = getStoresParticipatingNameToStoreMap();
    const dateOfOrder = $('#datePicker').val();
    const shoppingCartRows = $('#shoppingCartTable tbody tr');
    createItemsInOrderList(shoppingCartRows);
    let xCoordinateParsed = parseInt($('#destinationX').val());
    let yCoordinateParsed = parseInt($('#destinationY').val());
    loadDynamicOrderSummaryInformation(storesParticipatingInOrder, itemsInOrderList, xCoordinateParsed, yCoordinateParsed);
    createDynamicOrder(storesParticipatingInOrder, calculateTotalDeliveryCostForDynamicPurchase(storesParticipatingInOrder, xCoordinateParsed, yCoordinateParsed)
        , calculateOrderCostForDynamicPurchase(itemsInOrderList), xCoordinateParsed, yCoordinateParsed, itemsInOrderList, dateOfOrder);
}

function isDiscountExistsInSelectedStore(discountName, selectedStoreName) {
    const storeDiscounts = currentAvailableStoresMap[selectedStoreName].storeDiscounts;

    for (const itemIdToDiscount in storeDiscounts) {
        for (const index in storeDiscounts[itemIdToDiscount]) {
            const listOfDiscountsPerItem = storeDiscounts[itemIdToDiscount];
            if(listOfDiscountsPerItem[index].name === discountName.replace(/_/g, " ")) { return true; }
        }
    }

    return false;
}

function createItemInOrderMap(selectedStoreName) {
    let itemListOfSelectedStore = [];
    for (const item of itemsInOrderList) {
        if(item.wasPartOfDiscount === 'No') {
            if(dynamicItemToStoreMap[item.Id].storeToBuyFrom.name === selectedStoreName) {
                item.pricePerUnit = dynamicItemToStoreMap[item.Id].minPriceForItem;
                itemListOfSelectedStore.push(item);
            }
        }
        else {
            //Check if the item was bought as part of a discount in the selectedStoreName
            if(isDiscountExistsInSelectedStore(item.discountName, selectedStoreName)) {
                itemListOfSelectedStore.push(item);
            }
        }
    }

    return itemListOfSelectedStore;
}

function ajaxUploadStoresFeedback(currentFeedbackMap) {
    if(Object.keys(currentFeedbackMap).length > 0) {
        $.ajax({
            url : POST_FEEDBACKS_URL,
            data: {
                "feedbackMap" : JSON.stringify(currentFeedbackMap)
            },
            type: "POST",
            success: function(data) {
                $('#feedbackModal').modal('hide');
                alert(data.message);
            },
            error: function (data) {
                alert(data.message);
            }
        });
    }

    $('#feedbackModal').modal('hide');
    resetOrderDetails();
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
        if(discount !== null) {
            let amountToSubtract = discount.buyThis.quantity;
            currentCartBucketListOfItems[discount.buyThis.itemId] = currentCartBucketListOfItems[discount.buyThis.itemId] - amountToSubtract;
        }
        else { alert("server error discount could not be found!"); }

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
    if($('#itemNameSelectBox').val() !== undefined) {
        validateAmountOfItemAndAddToCart(itemId, amountOfItem);
    }
    else { alert('Please choose an item'); }

    $('#amountToAddTextInput').val("");
});

$('#pickStoreButton').click(() => {
    const pickedStoreForStaticPurchase = $('#storePickerSelectBox').val();
    clearAllTables();
    ajaxLoadAvailableItems(pickedStoreForStaticPurchase); // The items are being loaded to the table and the item select box in this method !
});

$('#showOrderSummaryButton').click(() => {
    const purchaseMethod = $('#purchaseMethodToggle').prop('checked') ? 'dynamic' : 'static';
    if( purchaseMethod === 'static') { generateStaticOrderAndFillOrderSummaryModal(); }
    else { generateDynamicOrderAndFillOrderSummaryModal(); }
});

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

$('#cancelOrderButton').click(() => {
    resetOrderDetails();
});

function createFeedbackRow(Id, storeName) {
    return $("<tr id='row" + storeName + "'>\n" +
        "<td>" + Id + "</td>\n" +
        "<td>" + storeName.replace(/_/g," ") + "</td>\n" +
        "<td>" +
            '<div class="form-group">\n' +
                '<div class="form-group">\n' +
                    '<fieldset class="rating">\n' +
                        '<input type="radio" id="star5' + storeName +'" name="rating" value="5"/><label style="padding-right: 1px;padding-left: 1px;" for="star5' + storeName +'" class="full"></label>\n' +
                        '<input type="radio" id="star4' + storeName +'" name="rating" value="4"/><label style="padding-right: 1px;padding-left: 1px;" for="star4' + storeName +'" class="full"></label>\n' +
                        '<input type="radio" id="star3' + storeName +'" name="rating" value="3"/><label style="padding-right: 1px;padding-left: 1px;" for="star3' + storeName +'" class="full"></label>\n' +
                        '<input type="radio" id="star2' + storeName +'" name="rating" value="2"/><label style="padding-right: 1px;padding-left: 1px;" for="star2' + storeName +'" class="full"></label>\n' +
                        '<input type="radio" id="star1' + storeName +'" name="rating" value="1"/><label style="padding-right: 1px;padding-left: 1px;" for="star1' + storeName +'" class="full"></label>\n' +
                    '</fieldset>\n' +
                 '</div>\n' +
            '</div>\n' +
        '</td>\n' +
        "<td>" +
            '<input class="form-control" type="text" id="review' + storeName + '" placeholder="Write a review here" style="padding-left: 12px;" disabled="">\n' +
        "</td>\n" +
        "<td>" +
            '<button id="btn'+ storeName + '" class="btn btn-primary" type="button">Add Review</button>\n' +
        "</td>" +
        "</tr>\n");
}

function getFeedback(order) {
    currentFeedbackMap = {};

    let feedbackRow;
    $('#feedbackModal tbody').empty();
    if(order.purchaseMethod === 'static') {
        $('#storeReviewSelectBox').attr('hidden', true);
        feedbackRow = createFeedbackRow(currentAvailableStoresMap[order.storeName].Id, order.storeName.replace(/\s/g,"_"));
        $('#feedbackModal tbody').append(feedbackRow);
        initializeOnStarClick('#review' + order.storeName.replace(/\s/g,"_"), feedbackRow);
        setOnClickForAddReviewButton(order.storeName);
    }
    else {
        $('#storeReviewSelectBox').removeAttr('hidden');
        $('#storeReviewSelectBox option').remove();
        let hiddenOption =  $('<option value="undefined" hidden>Select Store</option>"');
        $('#storeReviewSelectBox').append(hiddenOption);
        for (const store in order.storesParticipatingWithRelevantItems) {
            $('#storeReviewSelectBox').append(createStoreOption(currentAvailableStoresMap[store]));
        }
    }

    $('#feedbackModal').modal('show');
}

function setOnClickForAddReviewButton(storeName) {
    let buttonId = '#btn' + storeName.replace(/\s/g,"_");

    $(buttonId).click(() => {
        const feedbackRow = $('#feedbackModal tbody tr')[0];
        let rating = getStoreRating($(feedbackRow).find('[type="radio"]'));
        let review = $(feedbackRow).find('[type="text"]').val();
        if(rating > 0) {
            const dateOfFeedback = $('#datePicker').val();
            currentFeedbackMap[storeName] = {
                "storeName" : storeName,
                "rating" : rating,
                "reviewText" : review,
                "date" : dateOfFeedback
            }
            alert("Your feedback to " + storeName + " was updated successfully");
        }
        else { alert("You can't add a feedback with no rating"); }
    });
}

function onPurchaseSuccess(data) {
    alert(data.message);
    const order = $('#purchaseMethodToggle').prop('checked') ? dynamicOrder : staticOrder;
    getFeedback(order);
}

$('#performPurchaseButton').click(() => {
    let purchaseMethod = $('#purchaseMethodToggle').prop('checked') ? "dynamic" : "static";
    let order = purchaseMethod === 'dynamic' ? dynamicOrder : staticOrder;
    $.ajax({
        url : POST_ORDER_URL,
        data: {
            "purchaseMethod" : purchaseMethod,
            "order" : JSON.stringify(order)
        },
        type: "POST",
        success: function(data) {
            $('#showOrderSummaryModal').modal('hide');
            onPurchaseSuccess(data);
        },
        error: function (data) {
            alert(data.message);
        }
    });
});

function initializeOnStarClick(textInputId, feedbackRow) {
    let stars = $(feedbackRow).find('[type="radio"]');
    for (let i = 0; i < stars.length; i++) {
        stars[i].addEventListener('click', function () {
            i = this.value;
            $(textInputId).prop('disabled', false);
        });
    }
}

function getStoreRating(starsArray) {
    for (let i = starsArray.length - 1; i >= 0; i--){
        if($(starsArray[i]).prop('checked')){
            return 5 - i;
        }
    }

    return 0;
}

$('#saveStoresFeedBackModalButton').click(() => {
    ajaxUploadStoresFeedback(currentFeedbackMap);
});

$('#storeReviewSelectBox').on('change', () => {
    $('#feedbackModal tbody').empty();
    const selectedStore = $('#storeReviewSelectBox').val();

    if(selectedStore !== undefined && selectedStore !== null) {
        let feedbackRow = createFeedbackRow(currentAvailableStoresMap[selectedStore].Id, selectedStore.replace(/\s/g,"_"));
        $('#feedbackModal tbody').append(feedbackRow);
        let textInputId= '#review' + selectedStore.replace(/\s/g,"_");
        let rowId = "#row" + selectedStore.replace(/\s/g,"_");
        let stars = $(rowId).find('[type="radio"]');
        for (let i = 0; i < stars.length; i++) {
            stars[i].addEventListener('click', function () {
                i = this.value;
                $(textInputId).prop('disabled', false);
            });
        }
        let buttonId = '#btn' + selectedStore.replace(/\s/g,"_");
        $(buttonId).click(() => {
            const feedbackRow = $('#feedbackModal tbody tr')[0];
            let storeName = $(feedbackRow).find('td')[1].textContent;
            let rating = getStoreRating($(feedbackRow).find('[type="radio"]'));
            let review = $(feedbackRow).find('[type="text"]').val();
            if(rating > 0) {
                const dateOfFeedback = $('#datePicker').val();
                currentFeedbackMap[storeName] = {
                    "storeName" : storeName,
                    "rating" : rating,
                    "reviewText" : review,
                    "date" : dateOfFeedback
                }
                alert("Your feedback to " + storeName + " was updated successfully");
            }
            else { alert("You can't add a feedback with no rating"); }
        });
    }
});

$('#closeStoresFeedBackModalButton').click(() => {
    resetOrderDetails();
});

$('#feedbackModal').on('hide.bs.modal', () => {
    resetOrderDetails();
});

$('#resetOrderButton').click(() => {
    resetOrderDetails();
});






