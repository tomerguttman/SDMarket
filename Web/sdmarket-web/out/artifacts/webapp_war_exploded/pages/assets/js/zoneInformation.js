var SELECTED_ZONE_INFORMATION_URL = buildUrlWithContextPath("getSelectedZoneInformation");
var REFRESH_ZONE_INFORMATION_URL = buildUrlWithContextPath("getZoneInformation")
var currentZoneStores;
var currentZoneItems;

$(document).ready(function(){
    refreshZoneInformationPage();
    setInterval(refreshZoneInformationPage, 1000);
})

function createZoneOption(zoneName) {
    return $('<option value="' + zoneName + '">' + zoneName + '</option>"');
}

function updateZoneSelectBoxPicker(data) {
    for(var zoneName of data.zonesAvailable) {
        if (!($('#zoneSelectBox option[value=' + '"' + zoneName + '"]').length > 0)) {
            $("#zoneSelectBox").append(createZoneOption(zoneName));
        }
    }
}

function refreshZoneInformationPage() {
    $.ajax({
        url : REFRESH_ZONE_INFORMATION_URL,
        type: "GET",
        success: function(data) {
            updateZoneSelectBoxPicker(data);
        },
        error: function (data) {
            alert(data.message);
        }
    });
}

function updateStoresInSelectedZoneTable() {

    var storesInZone = currentZoneStores;
    $('#storesInSelectedZoneTable tbody').empty();
    for (var storeKey in storesInZone) {
        $('#storesInSelectedZoneTable tbody').append(createRowForStoreInSelectedZoneTable(storesInZone[storeKey]));
    }
}

function updateItemsInSelectedZoneTable() {
    var itemsInZone = currentZoneItems;
    $('#itemsInSelectedZoneTable tbody').empty();

    for (var itemKey in itemsInZone) {
        $('#itemsInSelectedZoneTable tbody').append(createRowForItemInSelectedZoneTable(itemsInZone[itemKey]));
    }
}

function updateZoneInformationPage(data) {
    $('#zoneOwnerLabel').html(data.ownerName);
    $('#numberOfStoresLabel').html(data.amountOfStoresInZone);
    $('#numberOfItemsLabel').html(data.amountOfItemTypesInZone);
    $('#totalRevenueLabel').html("$" + data.totalRevenue);
    updateStoresInSelectedZoneTable();
    updateItemsInSelectedZoneTable();
}

$("#displayZoneDetailsButton").click(() => {
    if($("#zoneSelectBox").val() !== ""){
        const selectedZone = $("#zoneSelectBox").val();
        if(selectedZone !== "" && selectedZone != null) {
            $.ajax({
                url : SELECTED_ZONE_INFORMATION_URL,
                data : {"selectedZone" : selectedZone},
                type: "GET",
                success: function(data) {
                    currentZoneStores = data.storesInZone;
                    currentZoneItems = data.itemsAvailableInZone;
                    updateZoneInformationPage(data);
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

function createDisplayItemsButton(storeId) {
    return '<a id="storeRow"' + storeId + ' class="btn btn-primary btn-lg btn-sm" role="button" data-toggle="modal"' +
        ' onclick="activateStoreDisplayItemsModal(' + storeId + ')">Display Items</a>';
}

function createItemRowForDisplayStoreItemsModal(item) {
    return $("<tr>\n" +
        "<td>" + item.Id + "</td>\n" +
        "<td>" + item.name + "</td>\n" +
        "<td>" + item.purchaseCategory + "</td>\n" +
        "<td>" + item.pricePerUnit + "</td>\n" +
        "<td>" + item.totalItemsSold + "</td>" +
        "</tr>\n");
}

function activateStoreDisplayItemsModal(storeId) {
   $('#displayStoreItemsModal tbody').empty();

    const currentStore = currentZoneStores[storeId];
    const itemsBeingSold = currentStore.itemsBeingSold;
    for (var itemKey in itemsBeingSold) {
        $('#displayStoreItemsModal tbody').append(createItemRowForDisplayStoreItemsModal(itemsBeingSold[itemKey]));
    }

    $('#displayStoreItemsModal').modal('show');
}

function createRowForStoreInSelectedZoneTable(store) {
    return $("<tr class='storeRow'>\n" +
        "<td>" + store.Id + "</td>\n" +
        "<td>" + store.name + "</td>\n" +
        "<td>" + store.ownerName + "</td>\n" +
        "<td> (" + store.storeLocation.x + ', ' + store.storeLocation.y + ')' + "</td>\n" +
        "<td>" + store.storeOrdersHistory.length + "</td>\n" +
        "<td>" + store.deliveryPpk + "</td>\n" +
        "<td>" + store.totalItemsRevenue + "</td>\n" +
        "<td>" + store.totalDeliveryRevenue + "</td>\n" +
        "<td>" + createDisplayItemsButton(store.Id) + "</td>\n" +
        "</tr>\n");
}

function createRowForItemInSelectedZoneTable(item) {
    return $("<tr class='storeRow'>\n" +
        "<td>" + item.Id + "</td>\n" +
        "<td>" + item.name + "</td>\n" +
        "<td>" + item.purchaseCategory + "</td>\n" +
        "<td>" + item.amountOfStoresSellingThisItem + "</td>\n" +
        "<td>" + item.averagePriceOfTheItem + "</td>\n" +
        "<td>" + item.totalItemsSold + "</td>\n" +
        "</tr>\n");
}

$("#openCreateStoreModalButton").click(() => {
    $('#createStoreModal tbody').empty();
    resetCreateStoreModal();

    for(var item of currentZoneItems) {
        $('#createStoreModal tbody').append(createRowForCreateStoreItemsTable(item));
    }
});




