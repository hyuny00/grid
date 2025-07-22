// Jquery Document
$(document).ready(function() {
    init();
});

function init() {
    // flatpickr Plugin
    $('.pickr').flatpickr({
      locale: 'ko',
      dateFormat: 'Y-m-d',
      onReady: function(selectedDates, dateStr, instance) {
      replaceYearWithSelect(instance);
    },
    onOpen: function(selectedDates, dateStr, instance) {
      replaceYearWithSelect(instance);
    }
    });

    // tab Event 1
    $('.tab').on('click', 'button', function () {
      let $btn = $(this);
      let $li = $btn.parent();
      let index = $li.index();
      let $tabItems = $('.tabCont').children().removeClass('active');
      let $target = $tabItems.eq(index).addClass('active');

      $li.addClass('active').siblings().removeClass('active');
      $li.find('button').addClass('active');
      $li.siblings().find('button').removeClass('active');

      if ($target.hasClass('depth')) {
        const $depth1 = $target.children().eq(0);
        const $depth2 = $target.children().eq(1);

        $depth1.children().removeClass('active').first().addClass('active');
        $depth2.children().removeClass('active').first().addClass('active');
      }
    });

    // tab Event 2
    $('.tabCont').on('click', '.depth2 button', function () {
      const $btn = $(this);
      const index = $btn.index();

      $btn.addClass('active').siblings().removeClass('active');

      const $depth2Cont = $btn.closest('.depth2').next('.depth2Cont');
      $depth2Cont.children().removeClass('active').eq(index).addClass('active');
    });

    // btn-flt Event
    $('.btn-flt').on('click', function () {
      $(this).toggleClass('on');
      $('.fltList').toggleClass('on');
      if (!$(this).hasClass('on')) {
        $('.fltList li').removeClass('on');
        $('.fltCont li').removeClass('on');
      }
    });

    $('.schBox.de .btn-flt').on('click', function () {
      $(this).children('span').toggleClass('off');
      $(this).parents('.schBox.de').toggleClass('on')
    });

    // fltList btn Event
    $('.fltList button').on('click', function () {
      let $li = $(this).parent();
      let ulIdx = $li.parent().index();
      let liIdx = $li.index();

      let isAlreadyOn = $li.hasClass('on');

      $('.fltList li, .fltCont li').removeClass('on');

      if (!isAlreadyOn) {
        $li.addClass('on');
        $('.fltCont ul').eq(ulIdx).children().eq(liIdx).addClass('on');
      }
    });

    // tbl Chk event
    $('.tbl .tblChk input[type="checkbox"]').on('change', function() {
      $(this).closest('tr').toggleClass('on', this.checked);
    });

    // contList btn Event
    $('.contList button').on('click', function(){
      $(this).toggleClass('on');
    });
    
    // searchBox input[type="text"] clear
    $('#iptTxt').on('input', function () {
      $('.btn-ipt').toggle(!!$(this).val().trim());
    }).trigger('input');
    
    $('.btn-ipt').on('click', () => $('#iptTxt').val('').trigger('input'));
    
    $('.mapList a').on('click', function(){
      $(this).toggleClass('on');
    });
    
    // popup Open btn
    $('button.pop').on('click', e => {
      $('#' + $(e.currentTarget).data('target')).fadeIn(300);
      $('.dim').fadeIn(300);
    });
    
    // popup Close btn
    $('.popup').on('click', '.btn-close, .close', e => {
      $(e.currentTarget).closest('.popup').fadeOut(300);
      $('.dim').fadeOut(300);
    });
    
    // toggleBox btn
    $('.toggleBox button').on('click', function(){
      $(this).parent().toggleClass('on');
    });
}

// pickr year input => select[Transform]
function replaceYearWithSelect(instance) {
    const container = instance.calendarContainer;
    const yearInputWrapper = container.querySelector(".flatpickr-current-month .numInputWrapper");

    if (!yearInputWrapper || yearInputWrapper.querySelector("select")) return;

    const input = yearInputWrapper.querySelector("input");
    const currentYear = parseInt(input.value, 10);

    // year Range
    const minYear = 1990;
    const maxYear = 2035;

    // input Delete
    input.style.display = "none";

    // select[Transform] 
    const yearSelect = document.createElement("select");
    for (let y = minYear; y <= maxYear; y++) {
      const opt = document.createElement("option");
      opt.value = y;
      opt.textContent = y + "년";
      if (y === currentYear) opt.selected = true;
      yearSelect.appendChild(opt);
    }

    yearSelect.addEventListener("change", function () {
      const newYear = parseInt(this.value);
      instance.changeYear(newYear);
    });

    yearInputWrapper.appendChild(yearSelect);
}

// SIDE(LNB)
$('.btnSlide').on('click', function(){
        $('#wrap').toggleClass('showMenu');
})

$('.side .gnb > li > a').on('click', function(){
        $(this).parent('li').toggleClass('active');
})

// USERBOX
$('.userBox .btnUser').on('click', function(){
        $(this).parent('.userBox').toggleClass('active');
})