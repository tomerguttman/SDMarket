var UPLOAD_XML_URL = buildUrlWithContextPath("uploadXML");
var REFRESH_DASHBOARD_URL = buildUrlWithContextPath("load-dashboard")


$(document).ready(function(){
    refreshDashboardInformation();
    setInterval(refreshDashboardInformation, 2000);
})

$("#uploadXMLButton").click(() => {
    if(document.getElementById("chooseXMLButton").textContent !== "Choose XML File"){
        var inputFile = new FormData();
        inputFile.append('file', $('#inputFile')[0].files[0]);
        $.ajax({
            url : UPLOAD_XML_URL,
            data : inputFile,
            processData: false,
            contentType: false,
            type: "POST",
            success: function(data) {
                alert(data.message);
                $("#inputFile")[0].value = "";
                document.getElementById("chooseXMLButton").textContent = "Choose XML File";
            },
            error: function (data) {
                alert(data.message);
                $("#inputFile")[0].value = "";
                document.getElementById("chooseXMLButton").textContent = "Choose XML File";
            }
        });
    }
    else{
        alert("Please choose a file first");
    }
});

function addTransactionToTransactionsOverview(transactionType, datePicked, amountToRecharge, balanceBefore, balanceAfter) {
    var newTransactionRow = $("<tr>\n" +
        "<td>" + transactionType + "</td>\n" +
        "<td>" + datePicked + "</td>\n" +
        "<td>" + "$" + amountToRecharge + "</td>\n" +
        "<td>" + "$" + balanceBefore + "</td>\n" +
        "<td>" +  "$" + balanceAfter + "</td>" +
        "</tr>\n");
    $('#transactionsOverviewTable tbody').append(newTransactionRow);
}

function refreshDashboardInformation() {
    $.ajax({
        type: 'GET',
        contentType: "application/json charset=utf-8",
        url: REFRESH_DASHBOARD_URL,
        success:function(data){
            //'data' is the value returned.
            loadNewDataToDashboard(data);
        },
        error:function(e){
            alert('An error was encountered.');
            console.log(e.responseText);
        }
    });
}

function updateDashboardLabels(currentEarning, storesOwned, ordersMadeFromOwnedStores, averageRating) {
    $("#earningsSpan").html("$" + currentEarning);
    $("#storesOwnedSpan").html(storesOwned);
    $("#ordersMadeFromOwnedStoresSpan").html(ordersMadeFromOwnedStores);
    $("#averageRatingSpan").html(averageRating);
    $("#currentBalanceSpan").html("$" + currentEarning);
}

function updateDashboardZonesTable(systemZones) {
    $("#zonesOverviewTable tbody").empty();
    if(systemZones != null) {
        for (var zone of systemZones) {
            $('#zonesOverviewTable tbody').append(createZonesTableRow(zone));
        }
    }
}

function updateDashboardTransactionsTable(userTransactions) {
    $("#transactionsOverviewTable tbody").empty();
    userTransactions.forEach((transaction) => addTransactionToTransactionsOverview(transaction.type, transaction.date, transaction.amount, transaction.balanceBefore, transaction.balanceAfter));
}

function updateDashboardActiveUsersTable(otherUsers) {
    $("#activeUsersTable tbody").empty();
    for (var user of otherUsers){
        $("#activeUsersTable tbody").append(createActiveUserTableRow(user));
    }
}

function loadNewDataToDashboard(data) {
    updateDashboardLabels(data.totalEarnings, data.amountOfStoresOwned, data.ordersMadeFromOwnedStores, data.averageRating);
    updateDashboardZonesTable(data.systemZones);
    updateDashboardTransactionsTable(data.userTransactions);
    updateDashboardActiveUsersTable(data.otherUsers);
}

function createZoneButton(zoneName) {
    const noSpaceZoneName = zoneName.replace(/ /g,"");
    return '<button class="btn btn-primary btn-sm" type="submit" name="zoneName" value=' + noSpaceZoneName + '>' +
        "Go To Zone" +
        "</button>";
}

function createZonesTableRow(zone) {
    return $("<tr>\n" +
        "<td>" + zone.zoneName + "</td>\n" +
        "<td>" + zone.ownerName + "</td>\n" +
        "<td>" + zone.amountOfItemTypesInZone + "</td>\n" +
        "<td>" + zone.amountOfStoresInZone + "</td>\n" +
        "<td>" + zone.amountOfOrdersInZone + "</td>" +
        "<td>" + zone.averageOrdersCostWithoutDelivery.toFixed(2) + "</td>" +
        "<td>" +
        "<form method='GET' action='/pages/order'>\n" +
        createZoneButton(zone.zoneName) +
        "</form>\n" +
        "</td>"+
        "</tr>\n");
}

function createActiveUserTableRow(user) {
    var realUserType;
    realUserType = user.userType === "customer" ? "Customer" : "Shop Owner";

    return $("<tr>\n" +
        "<td>" + user.name + "</td>\n" +
        "<td>" + realUserType + "</td>\n" +
        "</tr>\n");
}



