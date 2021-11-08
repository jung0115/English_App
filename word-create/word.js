import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { StyleSheet, Text, View, ScrollView } from 'react-native';

export default function App() {
  return (
    <View style={styles.container}>
        <View style={styles.wordText}>
            <Text style={styles.flex2}>단어</Text>
            <Text style={styles.flex1}>뜻</Text>
        </View>
        <View style={styles.wordList}>
            <ScrollView style={styles.flex2}>
                <Text>dkkk</Text>
            </ScrollView>
            <ScrollView style={styles.flex1}>
                <Text>아ㅏㅏ</Text>
            </ScrollView>
        </View>
        <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    marginLeft: 30,
    marginRight: 30
  },
  wordText: {
      marginTop: 60,
      borderWidth: 1,
      alignContent: 'center',
      flexDirection: 'row',
      justifyContent: 'center',
      alignItems: "center"
  },
  flex1: {
      flex: 1,
  },
  flex2: {
      flex: 1,
      paddingLeft: 40,
  },
  wordList: {
    borderWidth: 1,
    flexDirection: 'row',
    alignItems: "center",
    alignContent: 'center',
    justifyContent: 'center',
  }
});