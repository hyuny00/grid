/** * @license Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved. * For licensing, see LICENSE.md or http://ckeditor.com/license */
CKEDITOR.editorConfig = function( config )
{

	config.resize_enabled = false;
	config.language = 'ko';

	//config.skin = 'office2003';
	config.height = 350;

	config.enterMode=CKEDITOR.ENTER_BR;
	config.shiftEnterMode=CKEDITOR.ENTER_P;
	config.entities_latin=true;

	 config.filebrowserBrowseUrl = '/ckfinder/ckfinder.html';
	 //config.filebrowserImageBrowseUrl = '/ckfinder/ckfinder.html?type=Images';
	 //config.filebrowserFlashBrowseUrl = '/ckfinder/ckfinder.html?type=Flash';
	 //config.filebrowserUploadUrl = '/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Files';
	 config.filebrowserImageUploadUrl = '/file/uploadImage';
	 //config.filebrowserFlashUploadUrl = '/ckfinder/core/connector/java/connector.java?command=QuickUpload&type=Flash';

	 config.extraPlugins = 'font,justify,panel,button,floatpanel,colordialog,dialogui,colorbutton,panelbutton';
	config.toolbar = 'Full';
	config.toolbar_Full =
	 [

	  { name: 'document', items : [ 'Source','-','Save','NewPage','DocProps','Preview','Print','-','Templates' ] },
      { name: 'links', items : [ 'Link','Unlink','Anchor' ] },
	  { name: 'insert', items : [ 'Image','Table','HorizontalRule','Smiley','SpecialChar','PageBreak' ] },
	  { name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ] },
	  { name: 'class', items : [ 'Underline','Bold','Italic','Superscript','Subscript'  ] },
	  { name: 'styles', items : [ 'Styles','Font','FontSize' ] },
	  { name: 'colors', items : [ 'TextColor','BGColor' ] },
	 ];
	  config.font_names = '돋움; Nanum Gothic Coding; 맑은 고딕; 바탕; 궁서; Quattrocento Sans;' + CKEDITOR.config.font_names;	  config.filebrowserUploadMethod = 'form';
};
