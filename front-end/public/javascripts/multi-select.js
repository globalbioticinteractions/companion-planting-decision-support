// import 'select2'; 
// import 'script-loader!select2/dist/js/select2.js';
// import 'script-loader!select2/dist/js/i18n/fr.js';

$( '#multiple-select-field' ).select2( {
  theme: "bootstrap-5",
  width: $( this ).data( 'width' ) ? $( this ).data( 'width' ) : $( this ).hasClass( 'w-100' ) ? '100%' : 'style',
  placeholder: $( this ).data( 'placeholder' ),
  closeOnSelect: false,
} );