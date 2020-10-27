var UPLOAD_XML_URL = buildUrlWithContextPath("uploadXML");
var REFRESH_DASHBOARD_URL = buildUrlWithContextPath("load-dashboard")
var currentNotifications = [];

$(document).ready(function(){
    refreshDashboardInformation();
    setInterval(refreshDashboardInformation, 2000);
})

$("#uploadXMLButton").click(() => {
    if($("#inputFile").val() !== ""){
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
        "<td>" + "$" + amountToRecharge.toFixed(2) + "</td>\n" +
        "<td>" + "$" + balanceBefore.toFixed(2) + "</td>\n" +
        "<td>" +  "$" + balanceAfter.toFixed(2) + "</td>" +
        "</tr>\n");
    $('#transactionsOverviewTable tbody').append(newTransactionRow);
}

function refreshDashboardInformation() {
    $.ajax({
        type: 'GET',
        contentType: "application/json charset=utf-8",
        url: REFRESH_DASHBOARD_URL,
        data: { "amountOfNotifications" : currentNotifications.length },
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
    $("#earningsSpan").html("$" + currentEarning.toFixed(2));
    $("#storesOwnedSpan").html(storesOwned);
    $("#ordersMadeFromOwnedStoresSpan").html(ordersMadeFromOwnedStores);
    $("#averageRatingSpan").html(averageRating.toFixed(2));
    $("#currentBalanceSpan").html("$" + currentEarning.toFixed(2));
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
    updateDashboardNotificationsDropdownMenu(data.notifications) //all notifications must be new.

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

function updateDashboardNotificationsDropdownMenu(notifications) {
    notifications.forEach(notification => currentNotifications.push(notification));

    if(currentNotifications.length !== 0) {
        if($('#notificationsCounterSpan').text() === "0") {
            $('#dropDownNotificationsMenu a').remove();
        }

        for (const notification of notifications) {
            $('#dropDownNotificationsMenu').prepend(createDropdownMenuNotification(notification));
        }
    }
    else {
        $('#dropDownNotificationsMenu a').remove();
        let noNotifications = $('<a class="text-cetner dropdown-item small text-gray-500">No notifications to show</a>')
        $('#dropDownNotificationsMenu').append(noNotifications);
    }

    $('#notificationsCounterSpan').text(currentNotifications.length);
}

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