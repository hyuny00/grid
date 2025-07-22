$(document).ready(function(){
    uiInit();
})


function uiInit() {

    // select event
    $("#selectBox").select2();
    $('#example').on('select2:select', function(e) {
        var id = e.params.data.id;
        var value = $(this).val();
        if (id == 'ALL') {
            $('#example').val(null);
            $('#example').val('ALL').trigger('change');
        }
        else {
            $('#all').prop('selected', false).trigger('change');
        }
    });

    $('.select-tit').on('click', function(){
        $(this).next('ul').slideToggle(120);
        return false;
    });
    $('.selectbox a, .selectbox-s a').on('click', function(){
        let title = $(this).text();
        $(this).parents('.selectbox').children('.select-tit').text(title);
        $(this).parents('.selectbox-s').children('.select-tit').text(title);
        $(this).parents('.select-opt').slideUp(120);
        return false;
    });

 // left 메뉴 event
    $('.left-side-list > li ul li a').on('click', function(){
        $('.left-side-list > li').removeClass('active');
        $('.left-side-list > li ul li').removeClass('active');
        $(this).parent('li').addClass('active');
        $(this).parents('li').addClass('active');
    })

    // tab event
    $('.tab ul li').first().addClass('active');
    //$('.table-tab').hide();
    $('.fc-view.fc-timeGridWeek-view.fc-timeGrid-view').show();
    //$('#tab01').show();


    $('.tab ul li a').on('click', function(){
        let index = $(this).parents('li').index() + 1;
        $('.tab ul li').removeClass('active');
        $(this).parents('li').addClass('active');

        var offset = $('#tab0'+index).offset();
        $("html").animate({scrollTop : offset.top-130}, 400);
    })

    $('.tab-inner').first().addClass('active');
    $('.tab02 ul li, .tab03 ul li').first().addClass('active');
    $('.tab04 ul li').first().addClass('active');

    $('.tab02 ul li a, .tab03 ul li a, .tab04 ul li a').on('click', function(){
        let i = $(this).parent().index() + 1;

        $('.tab02 ul li, .tab03 ul li, .tab04 ul li').removeClass('active');
        $(this).parent().addClass('active');
        $(this).toggleClass('active');

        $('.tab-inner').removeClass('active');

        $('.tab-inner:nth-of-type('+ i + ')').addClass('active');
        console.log('index:'+ i);

        return false;
    })


    // tab > more-btn
    $('.tab03 .more-btn').on('click', function(){
    	$(this).next('.selectbox').toggle();
    })

    // search click event
    $('.list-option-filter a').on('click', function(){
        $('.list-option-filter a').removeClass('active');
        $(this).addClass('active');
    })

    // info-process event
    $('.info-process ul li a').on('click', function(){
        $('.info-process ul li').removeClass('active');
        $(this).parents('li').addClass('active');
    })

    // left sub 메뉴 아코디언 event
    $('.left-side-list > li > a').on('click', function(){
        let selected = $(this).next().is(':hidden');
        if( selected ){
            $('.left-side-list > li ul').slideUp(200);
            $('.left-side-menu .left-side-list > li').removeClass('active');
            $(this).next().slideDown(200);
            $(this).parents('li').addClass('active');
        } else {
            $('.left-side-menu .left-side-list li').removeClass('active');
            $('.left-side-menu .left-side-list > li ul').slideUp(170);
            $(this).next().slideUp(200);
        }
    })

    // gnb event
    // gnb, left-menu 클릭 연결
    $('.left-side-list').hide();
    //$('.header .gnb li').first().addClass('active');
    $('.left-side-list:nth-of-type(1)').show();

    $('.header .gnb li a').on('click', function(){
        let index = $(this).parents('li').index() + 1;

        $('.header .gnb li').removeClass('active');
        $(this).parent('li').addClass('active');

        $('.left-side-list').hide();
        $('.left-side-list:nth-of-type('+ index +')').fadeIn(150);
    })


    // table focus
    // table tr bg check
    $('.active tr').on('click', function(){

        if( $(this).hasClass('active') === false ){
            $(this).children('td').children('.inputbox').children('.checkbox').children('.check').addClass('active'); //check on
            $(this).addClass('active') //tr bg
        }else {
            $(this).children('td').children('.inputbox').children('.checkbox').children('.check').prop('checked',false); //check off
            $(this).children('td').children('.inputbox').children('.checkbox').children('.check').removeClass('active'); //check off
            $(this).removeClass('active') //tr bg
        }
    });

    // checkbox on/off
    $('.active tr td .check').on('click', function(){

        if( $(this).hasClass('active') === false ){
            $(this).addClass('active'); //check on
            $(this).parent().parent().parent().parent().addClass('active') // tr bg
        }else {
            $(this).removeClass('active'); //check off
            $(this).prop('checked',false); //check off
            $(this).parent().parent().parent().parent().removeClass('active') //tr bg
        }
    });

    // file event
    $('td .btn-add-delete + .file-list li a').on('click', function(){
        $(this).parent().toggleClass('active');
        return false;
    })
    if( $('.btn-add-delete').next('.file-list') != null ){
        $('.btn-add-delete').css({marginBottom : '10px'});
    }else {
        $('.btn-add-delete').css({marginBottom : '0'});
    }

    // 210902 쪽지함 받는이 more btn
    $('.btn-more-s').on('click',function(){
        let sendList = $(this).parent().parent().next().is(":hidden");
        if( sendList ){
            $(this).parent().parent().next().show();
        } else {
            $(this).parent().parent().next().hide();
        }
    })

    // file attactch event
    var uploadFile = $('.fileBox .exFile');
    uploadFile.on('change',function(){
        if(window.FileReader){
            var filename = $(this)[0].files[0].name;
        }else {
            var filename = $(this).val().split('/').pop().split('\\').pop();
        }
        $(this).siblings('.fileName').html(filename);
    });

    let title = $('a');
    title.mouseenter(function(){
        let tableTit = $(this).text();
        $(this).attr('title', tableTit);
    })

    // page 이동
    let noticeDetail = $('.table-list a');
    noticeDetail.on('click', function(){
        location.href = '#.html';
    })

    // tooltip
    $('#tooltip').mouseenter(function(){
        $(this).next('.tooltip').fadeIn(120);
    }).mouseleave(function(){
        $(this).next('.tooltip').fadeOut(120);
    })

    // popup event
    $('.btn-submit-s').on('click', function(){

    })

    // table col-resize
    $(function() {
	let startX,
        startWidth,
        $handle,
        $table,
        pressed = false;

	$(document).on({
		mousemove: function(event) {
			if (pressed) {
				$handle.width(startWidth + (event.pageX - startX));
			}
		},
		mouseup: function() {
			if (pressed) {
				$table.removeClass('resizing');
				pressed = false;
			}
		}
    }).on('mousedown', '.table th', function(event) {
            $handle = $(this);
            pressed = true;
            startX = event.pageX;
            startWidth = $handle.width();

            $table = $handle.closest('.table').addClass('resizing');
        }).on('dblclick', '.table thead', function() {
            // Reset column sizes on double click
            $(this).find('th[style]').css('width', '');
        });
    });

    // 팝업
    /*$('#popup').on('click', 'a', function(e){
        let item = e.target;
        e.preventDefault();
        $('.popup-box').fadeToggle(100);
        dim.fadeIn(100);
        console.log(this)
    })*/

    $('#popup13').on('click', function(){
        $('div#popup13').fadeToggle(100);
        dim.fadeIn(100);
    })
    $('#popup14').on('click', function(){
        $('div#popup14').fadeToggle(100);
        dim.fadeIn(100);
    })
    $('#popup15').on('click', function(){
        $('div#popup15').fadeToggle(100);
        dim.fadeIn(100);
    })
    $('#popup16').on('click', function(){
        $('div#popup16').fadeToggle(100);
        dim.fadeIn(100);
    })
    $('#popup17').on('click', function(){
        $('div#popup17').fadeToggle(100);
        dim.fadeIn(100);
    })
    $('#popup22').on('click', function(){
        $('div#popup22').fadeToggle(100);
        dim.fadeIn(100);
    })
    $('#popup23').on('click', function(){
        $('div#popup23').fadeToggle(100);
        dim.fadeIn(100);
    })
    $('#popup26').on('click', function(){
        $('div#popup26').fadeToggle(100);
        dim.fadeIn(100);
    })

    // 210902 추가
    $('#messagePop').on('click', function(){
        $('div#messagePop').fadeToggle(100);
        dim.fadeIn(100);
    })
    $('#fileViewer').on('click', function(){
        $('div#fileViewer').fadeToggle(100);
        dim.fadeIn(100);
    })
    $('#notiPop').on('click', function(){
        $('div#notiPop').fadeToggle(100);
        dim.fadeIn(100);
    })

    // dim add
    let dim = $('<div class="dim"></div>');
    let close = $('<button type="button"><i class="btn-close"></i></button>');
    $('body').append(dim);
    $('.popup-title').append(close);

    dim.on('click', function(){
        $('.popup-box').fadeOut(100);
        dim.fadeOut(100);
    })

    // popup, dropdown close
    $('.popup .popup-title button, .btn-close').on('click', function(){
        $('.popup-box, .dim').fadeOut(100);
        dim.fadeOut(100);
    })
    $('.dropNewEvent > li > a ').on('click', function(){
        $('#eventModal').fadeIn(100);
        dim.fadeIn(100);
        $('.dropNewEvent').fadeOut(100);
    })
    $('.dropNewEvent').mouseleave(function(){
        $(this).fadeOut(100);
    })

    // 드롭메뉴 클릭 이벤트
    $('.fc-row').on('click', function(e){
        let w = window.innerWidth,
            h = window.innerHeight,
            dropW = $('.dropNewEvent').width(),
            dropH = $('.dropNewEvent').height();
        // 메뉴 띄울 위치 세팅
        let posL = e.pageX -240,
            posT = e.pageY -130;
        // 메뉴가 화면 크기를 벗어나면 위치를 바꾸어 배치
        if( posL + dropW > w) posL -= dropW;
        if( posT + dropH > h) posT -= dropH;

        $('.dropNewEvent').css({
            "top" : posT,
            "left" : posL,
            "position" : "absolute"
        }).fadeIn(100);
    })

    $('.drop-close').on('click', function(){
        $('.dropNewEvent').fadeOut(100);
        dim.fadeOut(100);
    })


    // esc popup close
    $(document).keydown(function(e){
        if(e.keyCode == 27){
            $('.popup-box').fadeOut(100);
            dim.fadeOut(100);
        }
    })

    // .btn-toggle
    $('.btn-toggle-box .btn-toggle').on('click', function(){
        if( $('.check').is(":checked") ){
            $(this).text('승인');
        } else {
            $(this).text('반려');
            $(this).parents('tr').addClass('bgnone'); // 210902 추가
        }
    })

    // detail search
    $('div#detailSch').hide();
    $('#detailSch').on('click', function(){
        $('div#detailSch').slideToggle(180);
        return false;
    })

    // datepicker
    $('.datepicker').on('click', function(){
		$(this).datepicker();
	})

    // 210902 reply click event
    $('.btn-reply').on('click', function(){
        $(this).parents('.mycommentBox').next('.replyBox').toggle(60);
        return false;
    })

	function startDate() {
		$.datepicker.setDefaults($.datepicker.regional['ko']);
		$( "#startDate" ).datepicker({
			 changeMonth: true,
			 changeYear: true,
			 nextText: '다음 달',
			 prevText: '이전 달',
			 dayNames: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
			 dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
			 monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			 monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			 dateFormat: "yy-mm-dd",
			 maxDate: 0,
			 onClose: function( selectedDate ) {
				 $("#endDate").datepicker( "option", "minDate", selectedDate );
			 }
		});
	}
	startDate();

	function acceptDate() {
		$.datepicker.setDefaults($.datepicker.regional['ko']);
		$( "#acceptDate" ).datepicker({
			 changeMonth: true,
			 changeYear: true,
			 nextText: '다음 달',
			 prevText: '이전 달',
			 dayNames: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
			 dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
			 monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			 monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			 dateFormat: "yy-mm-dd",
			 maxDate: 0,
			 onClose: function( selectedDate ) {
				 $("#startDate").datepicker( "option", "minDate", selectedDate );
			 }
		});
	}
	acceptDate();

	function endDate() {
		$( "#endDate" ).datepicker({
			 changeMonth: true,
			 changeYear: true,
			 nextText: '다음 달',
			 prevText: '이전 달',
			 dayNames: ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'],
			 dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
			 monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			 monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			 dateFormat: "yy-mm-dd",
			 maxDate: 0,
			 onClose: function( selectedDate ) {
				 $("#startDate").datepicker( "option", "maxDate", selectedDate );
			 }
		});
	}
	endDate();

    /*
    const nav = document.getElementById('fixed');
    const rectTop = nav.getBoundingClientRect().top;

    window.addEventListener('scroll', function(){
      if (window.pageYOffset > rectTop) {
        nav.style.position = 'fixed';
        nav.style.top = 0;
        nav.style.border = '1px solid red';
      } else {
        nav.style.position = 'static';
        nav.style.top = '';
      }
    });*/

}