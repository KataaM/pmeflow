<template>

    <div class="wrapper">
        <div class="searchBar">
            <input id="searchQueryInput" type="text" name="searchQueryInput" placeholder="Search" value="" />
            <button id="searchQuerySubmit" type="submit" name="searchQuerySubmit">
                <v-icon>mdi-magnify</v-icon>
            </button>
        </div>
    </div>

    <!-- <div class="searchBar">
      
        <input type="text" v-model="input" placeholder="Search fruits..." />
    </div> -->


    <!-- <div class="item fruit" v-for="fruit in filteredList()" :key="fruit">
            <p>{{ fruit }}</p>
        </div>
        <div class="item error" v-if="input && !filteredList().length">
            <p>No results found!</p>
        </div> -->

    <!-- 
   -->
</template>

<script>
export default {
    name: "searchBar",
    inheritAttrs: false,
    props: {
    }
};
</script>

<style>
.wrapper {
    width: 100%;
    max-width: 15.25rem;
}

.label {
    font-size: .625rem;
    font-weight: 400;
    text-transform: uppercase;
    letter-spacing: +1.3px;
    margin-bottom: 1rem;
}

.searchBar {
    width: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;
}

#searchQueryInput {
    width: 100%;
    height: 2.8rem;
    outline: none;
    background: #f5f5f5;
    border: none;
    border-radius: 1.625rem;
    padding: 0 3.5rem 0 1.5rem;
    font-size: 1rem;
}

#searchQuerySubmit {
    width: 3.5rem;
    height: 2.8rem;
    margin-left: -3.5rem;
    background: none;
    border: none;
    outline: none;
}

#searchQuerySubmit:hover {
    cursor: pointer;
}
</style>