let popupCounter = 0;





function openPopup(url, width = 600, height = 400, options = {}) {
	
	
	
  popupCounter++;
  const popupId = `popup_${popupCounter}`;
  const zIndex = 9990 + popupCounter;

  const popupHtml = `
    <div id="${popupId}" class="popup-modal" style="z-index:${zIndex}">
      <div class="popup-content" style="width:${width}px; height:${height}px;">
        <span class="popup-close" data-popup-id="${popupId}">&times;</span>
        <iframe src="${url}" style="width:100%; height:100%; border:none;"></iframe>
      </div>
    </div>
  `;
  $('body').append(popupHtml);

  // 닫기 버튼
  $(`.popup-close[data-popup-id="${popupId}"]`).on('click', function () {
    closePopup(popupId);
  });

  // ESC 키 닫기
  $(document).on(`keydown.${popupId}`, function (e) {
    if (e.key === "Escape") {
      closePopup(popupId);
    }
  });

  // 배경 클릭 닫기 (옵션)
  if (options.closeOnBackgroundClick) {
    $(`#${popupId}`).on('click', function (e) {
      if ($(e.target).hasClass('popup-modal')) {
        closePopup(popupId);
      }
    });
  }
}

function closePopup(popupId) {
  $(`#${popupId}`).fadeOut(200, function () {
    $(this).remove();
  });

  // ESC 이벤트 해제
  $(document).off(`keydown.${popupId}`);
}
