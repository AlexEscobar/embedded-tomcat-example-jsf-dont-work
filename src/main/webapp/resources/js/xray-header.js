/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function setUnsetAreaColor() {
    let data = $(this).data('maphilight') || {};
    const selectedPartId = $(".annotation-area .ui-state-highlight .bodypartid-hidden").text();
    const sameId = selectedPartId === this.id;

    const tableRowsNumber = $(".ui-datatable-scrollable-body tbody").children().length;
//  an empty table will still have just one empty row
    const noTemporaryRows = tableRowsNumber === 1;
    if (data.alwaysOn !== true || (sameId && noTemporaryRows)) {
        data.alwaysOn = true;
    } else {
        data.alwaysOn = false;
    }
    unselectSelectedAreas(this.id);
    $(this).data('maphilight', data).trigger('alwaysOn.maphilight');
}

function setBodyPartListHeight(totalHeight) {
    const annotationArea = $(".annotation-area");
    annotationArea.css('max-height', `${totalHeight}px`);
    annotationArea.css('min-height', `${totalHeight}px`);
}         

function setImageDisplayHeight(totalHeight) {
    const imagePreview = $("#image-preview");
    const image = $("#image-preview img");
    imagePreview.height(totalHeight);
    image.height(totalHeight * 0.85);
}

// Note: this handler was made with the assumption that @jqueryElement has the css property scroll-behavior:smooth
// As a result, setting the scrollTop property of the element doesn't immidieatly change its value 
function setScrollHandler(jqueryElement) {
    this.element = jqueryElement;
    this.scrollLevel = 0;
//  scrolling position aimed for. Doesn't mean this is the current actual scroll position
    this.scrollTarget = 0;
    this.scrollDirection = '';
    this.isScrolling = false;
    this.directions = {
        UP: 'up',
        DOWN: 'down'
    };
    
    const wheelHandler = event => {
        const target = event.currentTarget;
        const isScrollingUp = event.originalEvent.deltaY < 0;
        throttledUpdateScroll(isScrollingUp); 
        //  prevent direct control over scrolling
        event.preventDefault();
        targetCheck(target);
    };
//  checks whether gallery was scrolled to target location yet and reacts accordignly  
    const targetCheck = target => {
        const hasReachedTarget = target.scrollTop === this.scrollTarget;
//      make sure gallery is being scrolled to the most updated value of this.scrollTarget  
        if(!hasReachedTarget) {  
            target.scrollTop = this.scrollTarget;
        } else {
            this.isScrolling = false;
        }        
    };
    
//  updates parameters responsible for determining where should the gallery scroll to  
    const updateScroll = isScrollingUp => {
        const maxLevel = Math.floor(this.element.children().length / 5);
        if(!isScrollingUp) {
            this.scrollLevel = Math.min(this.scrollLevel + 1, maxLevel);
            this.scrollDirection = this.directions.DOWN;
        }
        else { 
            this.scrollLevel = Math.max(this.scrollLevel - 1, 0);
            this.scrollDirection = this.directions.UP;
        }
        this.scrollTarget = 102 * this.scrollLevel;
        this.isScrolling = true; 
        console.log(this.scrollLevel)
    };
    
    const throttledUpdateScroll = _.throttle(updateScroll, 50, { 'trailing': false });
    jqueryElement.on('wheel', wheelHandler);
} 