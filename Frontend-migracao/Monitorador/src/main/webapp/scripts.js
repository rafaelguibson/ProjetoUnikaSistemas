$(document).ready(function() {
    // jquery para alternar submenus
    $('.sub-btn').click(function() {
        $(this).next('.sub-menu').slideToggle();
        $(this).find('.dropdown').toggleClass('rotate');
    });

    // jquery para recolher a sidebar
    $('.close-btn').click(function() {
        $('.side-bar').removeClass('active');
        $('.menu-btn').css("visibility", "visible");
        $('.side-bar').css("left", "-256px"); // Move a sidebar para fora da tela
    });

    //jquery que habilita o menu lateral e desabilita o menu-btn
    $('.menu-btn').click(function() {
        $('.side-bar').addClass('active');
        $('.menu-btn').css("visibility", "hidden");
        $('.side-bar').css("left", "0px"); // Move a sidebar para fora da tela
    });
});

